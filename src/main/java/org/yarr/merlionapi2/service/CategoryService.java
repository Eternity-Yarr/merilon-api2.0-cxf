package org.yarr.merlionapi2.service;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import https.api_merlion_com.dl.mlservice3.ArrayOfItemsAvailResult;
import https.api_merlion_com.dl.mlservice3.ArrayOfItemsResult;
import https.api_merlion_com.dl.mlservice3.ArrayOfString;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.MLPortProvider;
import org.yarr.merlionapi2.model.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CategoryService
{
    private final static Logger log = LoggerFactory.getLogger(CategoryService.class);
    private final MLPortProvider portProvider;

    private final Cache<String, Category> categoryCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();

    private final Cache<String, Stock> stockCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    @Autowired
    public CategoryService(MLPortProvider portProvider) {
        this.portProvider = portProvider;
    }


    public Category category(CatalogNode catalog) {
        return category(catalog.id());
    }
    public Category category(String catId) {
        Preconditions.checkNotNull(catId, "Shouldn't be null");
        try
        {
            return categoryCache.get(catId, new ItemsRetriever(catId, portProvider));
        } catch (ExecutionException e) {
            log.error("Got exception while retrieving list of items of category {}", catId);
            return new Category(new HashMap<>());
        }
    }

    public Stock stock(CatalogNode catalog, Set<String> ids) {
        try
        {
            return stockCache.get(catalog.id(), new ItemsStockRetriever(catalog.id(), portProvider, Optional.ofNullable(ids)));
        } catch (ExecutionException e) {
            log.error("Got exception while retrieving prices and quantities for category {}", catalog);
            return new Stock(new HashMap<>());
        }
    }

    private static class ItemsRetriever implements Callable<Category>{
        private static final RateLimiter getItemsLimiter = RateLimiter.create(1.0);
        private final MLPortProvider portProvider;
        private final String catId;
        public ItemsRetriever(String catId, MLPortProvider portProvider) {
            this.portProvider = portProvider;
            this.catId = catId;
        }

        @Override
        public Category call() throws Exception
        {
            return new Category(retrieve());
        }

        private Map<String, Item> retrieve()
        {
            double throttle = getItemsLimiter.acquire();
            log.debug("Waited {} seconds for getItems for catId={} rate limit", throttle, catId);
            ArrayOfItemsResult result = portProvider.get().getItems(catId, null, "ДОСТАВКА", 0, 10000, "");
            return result.getItem()
                    .parallelStream()
                    .filter(ir -> ir.getNo() != null)
                    .map(ir -> new Item(ir.getNo(), catId, ir.getVendorPart(), ir.getName(), ir.getBrand()))
                    .collect(Collectors.toMap(Item::id, Function.<Item>identity(), (i, i2) -> i));
        }
    }

    private static class ItemsStockRetriever implements Callable<Stock>{
        private static final RateLimiter getItemsAvailLimiter = RateLimiter.create(3.0);
        private MLPortProvider portProvider;
        private String catId;
        private Set<String> ids;

        public ItemsStockRetriever(String catId, MLPortProvider portProvider, Optional<Set<String>> ids) {
            this.portProvider = portProvider;
            this.catId = catId;
            this.ids = ids.orElseGet(HashSet::new);
        }

        @Override
        public Stock call() throws Exception
        {
            return new Stock(retrieve());
        }

        private Map<String, StockItem> retrieve()
        {
            double throttle = getItemsAvailLimiter.acquire();
            log.debug("Waited {} seconds for rate limit", throttle);
            ArrayOfString ids = new ArrayOfString();
            //FIXME: burp
            if(this.ids.isEmpty())
                ids = null;
            else
                this.ids.forEach(ids.getItem()::add);
            String shipmentDate = DateTime.now().plusDays(2).toString("dd-MM-yy");
            ArrayOfItemsAvailResult availResult = portProvider.get().getItemsAvail(catId, "ДОСТАВКА", shipmentDate, "true", ids);
            return availResult.getItem()
                    .parallelStream()
                    .filter(ia -> ia.getNo() != null)
                    .map(ia -> new StockItem((long)Math.ceil(ia.getPriceClientRUBMSK()), ia.getAvailableClient(), ia.getNo()))
                    .collect(Collectors.toMap(StockItem::id, Function.<StockItem>identity(), (i1, i2) -> i2));
        }
    }

}

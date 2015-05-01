package org.yarr.merlionapi2.directory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.model.*;
import org.yarr.merlionapi2.service.CatalogService;
import org.yarr.merlionapi2.service.CategoryService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class ItemsRepository
{
    private final static Logger log = LoggerFactory.getLogger(ItemsRepository.class);

    private final CategoryService categoryService;
    private final CatalogService catalogService;

    private Cache<String, StockAndItem> items = CacheBuilder
            .newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Autowired
    public ItemsRepository(CategoryService categoryService, CatalogService catalogService) {
        this.categoryService = categoryService;
        this.catalogService = catalogService;
    }

    public void update(Item item) {
        StockAndItem si = items.getIfPresent(item.id());
        if(si == null) si = new StockAndItem(item.id(), item, null);
        items.put(item.id(), new StockAndItem(si.id(), item, si.stock()));
    }

    public void update(StockItem stockItem) {
        StockAndItem si = items.getIfPresent(stockItem.id());
        if(si == null) si =  new StockAndItem(stockItem.id(), null, stockItem);
        items.put(stockItem.id(), new StockAndItem(si.id(), si.item(), stockItem));
    }

    public StockAndItem get(Bond bond) throws ExecutionException {
        StockAndItem si = items.getIfPresent(bond.merlionId());
        if(si == null)
        {
            CatalogNode cn = catalogService.get().nodes().get(bond.catId());
            categoryService.category(cn).all().forEach(this::update);
            categoryService.stock(cn).all().forEach(this::update);
            si = items.getIfPresent(bond.merlionId());
        }

        return si;
    }
}

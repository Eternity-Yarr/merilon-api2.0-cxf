package org.yarr.merlionapi2.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import https.api_merlion_com.dl.mlservice2.ArrayOfCatalogResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.MLPortProvider;
import org.yarr.merlionapi2.directory.Catalog;
import org.yarr.merlionapi2.model.CatalogNode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CatalogService
{
    private final static Logger log = LoggerFactory.getLogger(CatalogService.class);
    private Cache<Class, Catalog> catalogCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(12, TimeUnit.HOURS)
            .build();
    private final MLPortProvider portProvider;

    @Autowired
    public CatalogService(MLPortProvider portProvider) {
        this.portProvider = portProvider;
    }

    public Catalog get() {
        try
        {
            return catalogCache.get(Catalog.class, new CatalogRetriever(portProvider));
        } catch (ExecutionException e) {
            log.error("Got exception during requesting catalog", e);
            return new Catalog(new HashMap<>());
        }
    }

    public static class CatalogRetriever implements Callable<Catalog>
    {
        private final RateLimiter getCatalogLimiter = RateLimiter.create(1.0);
        private final MLPortProvider portProvider;

        public CatalogRetriever(MLPortProvider portProvider) {
            this.portProvider = portProvider;
        }

        @Override
        public Catalog call() throws Exception
        {
            return new Catalog(retrieve());
        }

        private Map<String, CatalogNode> retrieve() {
            double throttle = getCatalogLimiter.acquire();
            log.debug("Waited {} seconds for rate limit", throttle);
            ArrayOfCatalogResult result = portProvider.get().getCatalog("ALL");
            log.debug("Got {} catalog entries", result.getItem().size());

            return result
                    .getItem()
                    .parallelStream()
                    .map(x -> new CatalogNode(
                            x.getIDPARENT().equals("Order") ? null : x.getIDPARENT(),
                            x.getDescription(), x.getID()))
                    .collect(
                            Collectors.toMap(CatalogNode::id, Function.identity())
                    );
        }
    }
}

package org.yarr.merlionapi2.service;

import org.yarr.merlionapi2.directory.Catalog;

public class CatalogService
{
    public CatalogService() {
        CacheService.i().put(Catalog.class, new CatalogRetriever());
    }

    public Catalog get() {
        return CacheService.i().retrieve(Catalog.class);
    }

    public static CatalogService i() {
        return Lazy.service;
    }

    private static class Lazy {
        public static final CatalogService service = new CatalogService();
    }
}

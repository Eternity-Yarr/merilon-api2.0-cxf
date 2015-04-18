package org.yarr.merlionapi2.service;

import org.yarr.merlionapi2.MLPortProvider;
import org.yarr.merlionapi2.directory.Catalog;

public class CatalogService
{
    public CatalogService(MLPortProvider portProvider) {
        CacheService.i().put(Catalog.class, new CatalogRetriever(portProvider));
    }

    public Catalog get() {
        return CacheService.i().retrieve(Catalog.class);
    }
}

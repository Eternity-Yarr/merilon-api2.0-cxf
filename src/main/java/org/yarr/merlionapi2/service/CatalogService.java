package org.yarr.merlionapi2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.directory.Catalog;

@Service
public class CatalogService
{
    @Autowired
    public CatalogService(CatalogRetriever catalogRetriever) {
        CacheService.i().put(Catalog.class, catalogRetriever);
    }

    public Catalog get() {
        return CacheService.i().retrieve(Catalog.class);
    }
}

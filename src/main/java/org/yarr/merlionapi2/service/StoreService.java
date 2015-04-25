package org.yarr.merlionapi2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.persistence.Database;

@Service
public class StoreService
{
    private final Database db;
    private final int merlionSupplierId;
    private final int merlionStoreId;

    @Autowired
    public StoreService(Database db, ConfigService config) {
        this.db = db;
        merlionStoreId = config.merlionStoreId();
        merlionSupplierId = config.merlionSupplierId();
    }



}

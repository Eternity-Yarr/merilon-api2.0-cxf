package org.yarr.merlionapi2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.model.StockItem;
import org.yarr.merlionapi2.persistence.Database;
import org.yarr.merlionapi2.persistence.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public boolean alreadyInStock(int itemId) throws SQLException {
        Transaction t = new Transaction(db.c());
        return alreadyInStock(t, itemId);
    }

    public boolean alreadyInStock(Transaction t, int itemId) throws SQLException {
/*
SELECT count(1)
FROM my_availability
WHERE item_id = ? AND store_id != ? AND supplier_id != ? AND aviable > 0
 */
        String SQL =
                "SELECT count(1) \n" +
                "FROM my_availability \n" +
                "WHERE item_id = ? AND store_id != ? AND supplier_id != ? AND aviable > 0\n";
        PreparedStatement ps =t.ps(SQL);
        ResultSet rs = t.rs(ps);
        t.commitAndCleanup();
        return true;
    }
}

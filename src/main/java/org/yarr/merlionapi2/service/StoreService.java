package org.yarr.merlionapi2.service;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.persistence.Database;
import org.yarr.merlionapi2.persistence.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class StoreService
{
    private static Logger log = LoggerFactory.getLogger(StoreService.class);
    private final Database db;
    private final int merlionSupplierId;
    private final int merlionStoreId;

    @Autowired
    public StoreService(Database db, ConfigService config) {
        this.db = db;
        merlionStoreId = config.merlionStoreId();
        merlionSupplierId = config.merlionSupplierId();
        Preconditions.checkArgument(merlionStoreId != 0, "System isn't properly configured, store_id is 0");
        Preconditions.checkArgument(merlionSupplierId != 0, "System isn't properly configured, supplier_id is 0");
    }

    public boolean alreadyInStock(int itemId) throws SQLException {
        Transaction t = new Transaction(db.c());
        boolean val = alreadyInStock(t, itemId);
        t.cleanupAndCommit();
        return val;
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
        PreparedStatement ps = t.ps(SQL);
        ps.setInt(1, itemId);
        ps.setInt(2, merlionStoreId);
        ps.setInt(3, merlionSupplierId);
        ResultSet rs = t.rs(ps);
        return rs.next();
    }

    public Optional<Integer> getPrice(int itemId) throws SQLException {
        Transaction t = new Transaction(db.c());
        Optional<Integer> val = getPrice(t, itemId);
        t.cleanupAndCommit();
        return val;
    }

    public Optional<Integer> getPrice(Transaction t, int itemId) throws SQLException {
/*
SELECT price FROM b_catalog_price WHERE product_id = ?
 */
        String SQL = "SELECT price FROM b_catalog_price WHERE product_id = ?";
        PreparedStatement ps = t.ps(SQL);
        ps.setInt(1, itemId);
        ResultSet rs = t.rs(ps);
        if(rs.next()) {
            return Optional.of(rs.getInt(1));
        } else {
            return Optional.absent();
        }
    }

    public void setPrice(int itemId, int price) throws SQLException {
        Transaction t = new Transaction(db.c());
        setPrice(t, itemId, price);
        t.cleanupAndCommit();
    }

    public void setPrice(Transaction t, int itemId, int price) throws SQLException {
        Preconditions.checkArgument(price != 0, "Tried to set price to 0");
/*
UPDATE b_catalog_price SET price = ? WHERE product_id = ?
 */
        String SQL = "UPDATE b_catalog_price SET price = ? WHERE product_id = ?";
        PreparedStatement ps = t.ps(SQL);
        ps.setInt(1, price);
        ps.setInt(2, itemId);
        int rowsUpdated = ps.executeUpdate();
        if(rowsUpdated != 1) {
            log.warn("Updated {} rows instead of 1 during modification of price of {} product_id", itemId);
        }
    }

    public void cleanStock(Transaction t) throws SQLException {
/*
 UPDATE my_availability SET aviable = 0 WHERE supplier_id = ? AND store_id = ?
 */
        String SQL = "UPDATE my_availability SET aviable = 0 WHERE supplier_id = ? AND store_id = ?";
        PreparedStatement ps = t.ps(SQL);
        ps.setInt(1, merlionSupplierId);
        ps.setInt(2, merlionStoreId);
        ps.executeUpdate();
    }
}

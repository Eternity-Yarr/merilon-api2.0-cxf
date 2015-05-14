package org.yarr.merlionapi2.service;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.model.Item;
import org.yarr.merlionapi2.persistence.Database;

import java.sql.*;
import java.util.Optional;

@Service
public class BitrixService
{
    private final Database db;
    private final int merlionSupplierId;
    private final int merlionStoreId;

    @Autowired
    public BitrixService(Database db, ConfigService config) {
        merlionStoreId = config.merlionStoreId();
        merlionSupplierId = config.merlionSupplierId();
        Preconditions.checkArgument(merlionStoreId != 0, "System isn't properly configured, store_id is 0");
        Preconditions.checkArgument(merlionSupplierId != 0, "System isn't properly configured, supplier_id is 0");
        this.db = db;
    }

    public Optional<Item> getByCode(String id) {
/*
SELECT bie.id, bie.name, bie.searchable_content, biep.value as code, article.value as article FROM b_iblock_element_property biep
LEFT JOIN b_iblock_element bie ON biep.iblock_element_id = bie.id
LEFT JOIN (SELECT iblock_element_id as id, value FROM b_iblock_element_property WHERE iblock_property_id = 4) article
ON  article.id = biep.iblock_element_id
WHERE iblock_property_id = 200 AND biep.VALUE = ?
 */
        String SQL =
                "SELECT bie.id, bie.name, bie.searchable_content, biep.value as code, article.value as article FROM b_iblock_element_property biep\n" +
                "LEFT JOIN b_iblock_element bie ON biep.iblock_element_id = bie.id\n" +
                "LEFT JOIN (SELECT iblock_element_id as id, value FROM b_iblock_element_property WHERE iblock_property_id = 4) article\n" +
                "ON  article.id = biep.iblock_element_id\n" +
                "WHERE iblock_property_id = 200 AND biep.VALUE = ?\n";
        try(Connection c = db.c();
            PreparedStatement ps = c.prepareStatement(SQL)
            ) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                    return Optional.of(from(rs));
                else
                    return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Item> getById(String id) {
/*
SELECT bie.id, bie.name, bie.searchable_content, biep.value as code, article.value as article FROM b_iblock_element_property biep
LEFT JOIN b_iblock_element bie ON biep.iblock_element_id = bie.id
LEFT JOIN (SELECT iblock_element_id as id, value FROM b_iblock_element_property WHERE iblock_property_id = 4) article
ON  article.id = biep.iblock_element_id
WHERE iblock_property_id = 200 AND bie.id = ?
 */
        String SQL =
                "SELECT bie.id, bie.name, bie.searchable_content, biep.value as code, article.value as article FROM b_iblock_element_property biep\n" +
                        "LEFT JOIN b_iblock_element bie ON biep.iblock_element_id = bie.id\n" +
                        "LEFT JOIN (SELECT iblock_element_id as id, value FROM b_iblock_element_property WHERE iblock_property_id = 4) article\n" +
                        "ON  article.id = biep.iblock_element_id\n" +
                        "WHERE iblock_property_id = 200 AND bie.id = ?\n";
        try(Connection c = db.c();
            PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    return Optional.of(from(rs));
                else
                    return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Double> getPriceById(String code) {
        String SQL = "SELECT price FROM b_catalog_price WHERE product_id = ?";
        try(Connection c = db.c();
            PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setString(1, code);
            try(ResultSet  rs = ps.executeQuery()) {
                if(rs.next())
                    return Optional.of(rs.getDouble("price"));
                else
                    return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPriceById(String code, long price)
    {
        Preconditions.checkArgument(price != 0, "tried to set price to 0");
        String SQL = "UPDATE b_catalog_price SET price =  ? WHERE product_id = ?";
        try (Connection c = db.c();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setLong(1, price);
            ps.setString(2, code);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setQuantityById(String code, int quantity) {
        Optional<Integer> id = getAvailabilityId(code);
        if(id.isPresent()) {
            String SQL = "UPDATE my_availability SET aviable = ?, date = NOW() WHERE id = ?";
            try (Connection c = db.c();
                PreparedStatement ps = c.prepareStatement(SQL)) {
                ps.setInt(1, quantity);
                ps.setInt(2, id.get());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            String SQL = "INSERT INTO my_availability (item_id, store_id, aviable, matching_id, supplier_id, date) VALUES (?, ?, ?, NULL, ?, NOW())";
            try (Connection c = db.c();
                 PreparedStatement ps = c.prepareStatement(SQL)) {
                ps.setString(1, code);
                ps.setInt(2, merlionStoreId);
                ps.setInt(3, quantity);
                ps.setInt(4, merlionSupplierId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Optional<Integer> getAvailabilityId(String code) {
        String SQL = "SELECT id FROM my_availability WHERE item_id = ? and supplier_id = ?";
        try (Connection c = db.c();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setString(1, code);
            ps.setInt(2, merlionSupplierId);
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    return Optional.of(rs.getInt("id"));
                else
                    return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Item from(ResultSet rs) throws SQLException {
        String name = String.format("[%s] %s", rs.getString("code"), rs.getString("name"));
        return new Item(rs.getString("article"), rs.getString("id"), name, "");
    }
}

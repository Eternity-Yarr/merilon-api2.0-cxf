package org.yarr.merlionapi2.service;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.model.Item;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class BitrixService
{
    private final static Logger log = LoggerFactory.getLogger(BitrixService.class);
    private final int merlionSupplierId;
    private final int merlionStoreId;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BitrixService(ConfigService config, DataSource dataSource) {
        merlionStoreId = config.merlionStoreId();
        merlionSupplierId = config.merlionSupplierId();
        Preconditions.checkArgument(merlionStoreId != 0, "System isn't properly configured, store_id is 0");
        Preconditions.checkArgument(merlionSupplierId != 0, "System isn't properly configured, supplier_id is 0");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
        try {
            return Optional.of(jdbcTemplate.queryForObject(SQL, new ItemMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Item> getById(String id) {
/*
SELECT bie.id, bie.name, bie.searchable_content, '-' as code, article.value as article FROM b_iblock_element_property biep
LEFT JOIN b_iblock_element bie ON biep.iblock_element_id = bie.id
LEFT JOIN (SELECT iblock_element_id as id, value FROM b_iblock_element_property WHERE iblock_property_id = 4) article
ON  article.id = biep.iblock_element_id
WHERE bie.id = ? GROUP BY bie.id
 */
        String SQL =
                "SELECT bie.id, bie.name, bie.searchable_content, '-' as code, article.value as article FROM b_iblock_element_property biep\n" +
                "LEFT JOIN b_iblock_element bie ON biep.iblock_element_id = bie.id\n" +
                "LEFT JOIN (SELECT iblock_element_id as id, value FROM b_iblock_element_property WHERE iblock_property_id = 4) article\n" +
                "ON  article.id = biep.iblock_element_id\n" +
                "WHERE bie.id = ? GROUP BY bie.id";
        try {
            return Optional.of(jdbcTemplate.queryForObject(SQL, new ItemMapper(), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Long> getPriceById(String code) {
        try {
            String SQL = "SELECT price FROM b_catalog_price WHERE product_id = ?";
            return Optional.of((long)Math.ceil(jdbcTemplate.queryForObject(SQL, Double.class, code)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void setPriceById(String code, long price)
    {
        Preconditions.checkArgument(price != 0, "tried to set price to 0");
        String SQL = "UPDATE b_catalog_price SET price =  ? WHERE product_id = ?";
        jdbcTemplate.update(SQL, price, code);
    }

    /**
     * Set item quantity
     * @return true if inserted new item, false if updated the old one
     */
    public boolean setQuantityById(String code, int quantity) {
        Optional<Integer> id = getAvailabilityId(code);
        if(id.isPresent()) {
            String SQL = "UPDATE my_availability SET aviable = ?, date = NOW() WHERE id = ?";
            jdbcTemplate.update(SQL, quantity, id.get());
            return false;
        } else {
            String SQL = "INSERT INTO my_availability (item_id, store_id, aviable, matching_id, supplier_id, date) VALUES (?, ?, ?, NULL, ?, NOW())";
            jdbcTemplate.update(SQL, code, merlionStoreId, quantity, merlionSupplierId);
            return true;
        }
    }

    public Optional<Integer> getQuantityById(String code) {
        String SQL = "SELECT aviable FROM my_availability WHERE id = ?";

        try {
            return Optional.of(jdbcTemplate.queryForObject(SQL, Integer.class, code));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Boolean> alreadyInStock(String id, int merlionSupplierId) {
        String SQL =
                "SELECT COUNT(*) > 0 AS cnt " +
                "FROM my_availability " +
                "WHERE item_id = ? " +
                "AND aviable > 0 " +
                "AND supplier_id != ?";
        try
        {
            return Optional.of(jdbcTemplate.queryForObject(SQL, Boolean.class, id, merlionSupplierId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private Optional<Integer> getAvailabilityId(String code) {
        log.trace("Getting availability for item_id={} and supplier_id={}", code, merlionSupplierId);
        String SQL = "SELECT id FROM my_availability WHERE item_id = ? and supplier_id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(SQL, Integer.class, code, merlionSupplierId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static class ItemMapper implements RowMapper<Item> {

        @Override
        public Item mapRow(ResultSet rs, int i) throws SQLException
        {
            String name = String.format("[%s] %s", rs.getString("code"), rs.getString("name"));
            return new Item(rs.getString("id"), "", rs.getString("article"), name, "");
        }
    }
}

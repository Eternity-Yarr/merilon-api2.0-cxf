package org.yarr.merlionapi2.service;

import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.model.Item;
import org.yarr.merlionapi2.persistence.Database;
import org.yarr.merlionapi2.persistence.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class BitrixService
{
    private Database db;

    public BitrixService(Database db)
    {
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
        Transaction t = null;
        Item ret = null;
        try {
            t = new Transaction(db.c());
            PreparedStatement ps = t.ps(SQL);
            ps.setString(1, id);
            ResultSet rs = t.rs(ps);
            if(rs.next())
                ret = new Item(rs.getString("article"), rs.getString("code"), rs.getString("name"), "");
            t.cleanupAndCommit();
        } catch (SQLException e) {
            try
            {
                if (t != null)
                    t.cleanupAndRollback();
            } catch (SQLException ignored) {}
        }
        return Optional.ofNullable(ret);
    }
}

package org.yarr.merlionapi2.scheduler;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.Bond;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.persistence.Database;
import org.yarr.merlionapi2.persistence.Transaction;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.StoreService;
import org.yarr.merlionapi2.service.TrackService;

import java.sql.SQLException;


@Component
@Scope("prototype")
public class RefreshBindingsInfoTask implements Runnable
{
    private static final Logger log = LoggerFactory.getLogger(RefreshBindingsInfoTask.class);
    private final BindService bindService;
    private final TrackService trackService;
    private final StoreService storeService;
    private final Transaction t;

    public RefreshBindingsInfoTask(
            BindService bindService,
            TrackService trackService,
            StoreService storeService,
            Database db) throws SQLException {
        this.bindService = bindService;
        this.trackService = trackService;
        this.storeService = storeService;
        this.t = new Transaction(db.c());
    }
    @Override
    public void run()
    {
        try
        {
            storeService.cleanStock(t);
            trackService
                .all()
                .nodes()
                .stream().sequential()
                .map(category -> new Pair<>(bindService.get(category.id()), category))
                .forEach(
                    bonds -> bonds.getKey()
                        .stream().sequential()
                        .forEach(
                            x -> refreshStockInfo(x, bonds.getValue()))
                );
            t.cleanupAndCommit();
        } catch (Exception e) {
            log.error("Exception! Rolling back changes", e);
            try
            {
                t.cleanupAndRollback();
            } catch (SQLException se) {
                log.error("Exception during rollback", se);
            }
        }
    }

    private void refreshStockInfo(Bond bond, CatalogNode cn) {

    }
}

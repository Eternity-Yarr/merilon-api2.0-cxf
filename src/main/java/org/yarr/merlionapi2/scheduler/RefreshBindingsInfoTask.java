package org.yarr.merlionapi2.scheduler;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.Bindings;
import org.yarr.merlionapi2.model.Bond;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.StoreService;
import org.yarr.merlionapi2.service.TrackService;


@Component
@Scope("prototype")
public class RefreshBindingsInfoTask implements Runnable
{
    BindService bindService;
    TrackService trackService;
    StoreService storeService;
    @Override
    public void run()
    {
        trackService
            .all()
            .nodes()
            .stream().sequential()
            .map(category -> bindService.get(category.id()))
            .forEach(
                bonds -> bonds
                    .stream().sequential()
                    .forEach(this::refreshStockInfo)
            );
        Bindings bindings = bindService.all();
        for(CatalogNode node: trackService.all().nodes())
        {

        }
    }

    private void refreshStockInfo(Bond bond) {

    }
}

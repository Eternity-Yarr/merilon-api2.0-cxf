package org.yarr.merlionapi2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.scheduler.TaskQueue;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.CatalogService;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/_monitor")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class MonitorRest
{
    private final CatalogService catalogService;
    private final TrackService trackService;
    private final BindService bindService;

    @Autowired
    public MonitorRest(CatalogService catalogService, TrackService trackService,
                       BindService bindService) {
        this.catalogService = catalogService;
        this.trackService = trackService;
        this.bindService = bindService;
    }

    @GET
    @Path("/")
    public Map<String, Object> status() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("Tasks in queue", TaskQueue.i().size());
        stats.put("Catalogs count", catalogService.get().nodes().size());
        stats.put("Currently tracking categories", trackService.all().nodes());
        stats.put("Bindings", bindService.all().bonds().size());
        return stats;
    }
}

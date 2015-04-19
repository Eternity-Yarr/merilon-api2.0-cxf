package org.yarr.merlionapi2.rest;

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
public class MonitorRest
{
    @GET
    @Path("/")
    public Map<String, Object> status() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("Tasks in queue", TaskQueue.i().size());
        stats.put("Entries in catalog", CatalogService.i().get().nodes().size());
        stats.put("Currently tracking categories", TrackService.i().all().nodes().size());
        stats.put("Bindings", BindService.i().all().bonds().size());
        return stats;
    }
}

package org.yarr.merlionapi2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.persistence.Database;
import org.yarr.merlionapi2.scheduler.TaskQueue;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.CatalogService;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
    private final Database db;
    @Autowired
    public MonitorRest(CatalogService catalogService,
                       TrackService trackService,
                       BindService bindService,
                       Database database) {
        this.catalogService = catalogService;
        this.trackService = trackService;
        this.bindService = bindService;
        this.db = database;
    }

    @GET
    @Path("/")
    public Map<String, Object> status() {
        boolean dbAccessible = false;
        String error = "";
        try(Connection con = db.c();
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("SELECT 1")) {
            if(rs.next() && rs.getInt(1) == 1)
                dbAccessible = true;
        } catch (SQLException e) {
            error = e.getClass().getName() + ": " + e.getMessage();
        }
        Map<String, Object> stats = new HashMap<>();
        stats.put("Tasks in queue", TaskQueue.i().size());
        stats.put("Catalogs count", catalogService.get().nodes().size());
        stats.put("Currently tracking categories", trackService.all().nodes());
        stats.put("Bindings", bindService.all().bonds().size());
        stats.put("Database accessible", dbAccessible);
        if(!error.isEmpty())
            stats.put("Errors", error);
        return stats;
    }
}

package org.yarr.merlionapi2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.service.CatalogService;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Path("/mlcatalogs")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class CatalogRest
{
    private final CatalogService catalogService;
    private final TrackService trackService;

    @Autowired
    public CatalogRest(CatalogService catalogService, TrackService trackService) {
        this.catalogService = catalogService;
        this.trackService = trackService;
    }

    @GET
    @Path("/")
    public List<CatalogNode> all(@QueryParam("filtered") boolean filtered) {
        List<CatalogNode> list = new ArrayList<>(catalogService.get().nodes().values());
        if(filtered)
            list = list.stream().filter(cat -> trackService.tracked(cat.id())).collect(Collectors.toList());
        Collections.sort(list);
        return list;
    }
}

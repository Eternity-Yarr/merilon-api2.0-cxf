package org.yarr.merlionapi2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.service.CatalogService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/catalog")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class CatalogRest
{
    private final CatalogService catalogService;

    @Autowired
    public CatalogRest(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GET
    @Path("/")
    public List<CatalogNode> all() {
        List<CatalogNode> list = new ArrayList<>(catalogService.get().nodes().values());
        Collections.sort(list);
        return list;
    }
}

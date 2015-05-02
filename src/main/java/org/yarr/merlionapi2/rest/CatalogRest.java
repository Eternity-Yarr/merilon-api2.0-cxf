package org.yarr.merlionapi2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.service.CatalogService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Map;

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
    public Collection<CatalogNode> all() {
        return catalogService.get().nodes().values();
    }
}

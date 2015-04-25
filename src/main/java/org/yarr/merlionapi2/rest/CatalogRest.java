package org.yarr.merlionapi2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.service.CatalogService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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
    public Map<String, CatalogNode> all() {
        return catalogService.get().nodes();
    }


}

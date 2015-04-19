package org.yarr.merlionapi2.rest;

import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.service.CatalogService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/catalog")
@Produces(MediaType.APPLICATION_JSON)
public class CatalogRest
{
    CatalogService service = CatalogService.i();

    @GET
    @Path("/")
    public Map<String, CatalogNode> all() {
        return service.get().nodes();
    }


}

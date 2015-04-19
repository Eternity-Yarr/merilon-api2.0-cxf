package org.yarr.merlionapi2.rest;

import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.model.TrackedNodes;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/track")
@Produces(MediaType.APPLICATION_JSON)
public class TrackRest
{
    private TrackService service = TrackService.i();

    @GET
    @Path("/")
    public TrackedNodes list() {
      return service.all();
    }

    @PUT
    @Path("/")
    public TrackedNodes add(@QueryParam("id") String id) {
        return service.track(new CatalogNode(null, "testnode", id));
    }

    @DELETE
    @Path("/")
    public TrackedNodes discard(@QueryParam("id") String id) {
        return service.discard(new CatalogNode(null, "testnode", id));
    }
}

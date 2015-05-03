package org.yarr.merlionapi2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.model.TrackedNodes;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/track")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Component
public class TrackRest
{
    private final TrackService service;

    @Autowired
    public TrackRest(TrackService service) {
        this.service = service;
    }

    @GET @Path("/")
    public Map<String, CatalogNode> list() {
      return service.all().nodes();
    }

    @PUT @Path("/{id}")
    public CatalogNode add(@PathParam("id") String id, CatalogNode catalogNode) {
        return service.track(new CatalogNode(catalogNode.parentId(), catalogNode.name(), catalogNode.id()));
    }

    @DELETE @Path("/{id}")
    public TrackedNodes discard(@PathParam("id") String id) {
        return service.discard(id);
    }

}

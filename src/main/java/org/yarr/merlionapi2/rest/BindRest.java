package org.yarr.merlionapi2.rest;

import com.google.common.base.Preconditions;
import org.apache.cxf.common.util.StringUtils;
import org.yarr.merlionapi2.model.Bindings;
import org.yarr.merlionapi2.model.Bond;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/bind")
@Produces(MediaType.APPLICATION_JSON)
public class BindRest
{
    @GET @Path("/")
    public Bindings all(@QueryParam("catId") String catalogId) {
        Preconditions.checkNotNull(catalogId, "catId parameter is required");
        Preconditions.checkState(!catalogId.isEmpty(), "catId shouldn't be empty");
        return new Bindings(catalogId, new ArrayList<>());
    }

    @PUT @Path("/create")
    public Bindings bind(@QueryParam("catId") String catalogId, @QueryParam("merlId") String merlionId, @QueryParam("id") String id){
        Preconditions.checkNotNull(catalogId, "catId parameter is required");
        Preconditions.checkNotNull(merlionId, "merlId parameter is required");
        Preconditions.checkNotNull(id, "id parameter is required");
        Preconditions.checkState(!catalogId.isEmpty(), "catId parameter shouldn't be empty");
        Preconditions.checkState(!merlionId.isEmpty(), "merlId parameter shouldn't be empty");
        Preconditions.checkState(!id.isEmpty(), "id parameter shouldn't be empty");
        List<Bond> bonds = new ArrayList<>();
        bonds.add(new Bond(merlionId, id));
        return new Bindings(catalogId, bonds);
    }

    @PUT @Path("/")
    public Bindings stage(@QueryParam("catId") String catalogId, @QueryParam("merlId") String merlionId) {
        Preconditions.checkNotNull(catalogId, "catId parameter is required");
        Preconditions.checkNotNull(merlionId, "merlId parameter is required");
        Preconditions.checkState(!catalogId.isEmpty(), "catId parameter shouldn't be empty");
        Preconditions.checkState(!merlionId.isEmpty(), "merlId parameter shouldn't be empty");
        List<Bond> bonds = new ArrayList<>();
        bonds.add(new Bond(merlionId, null));
        return new Bindings(catalogId, bonds);
    }

    @DELETE @Path("/")
    public Bindings unbind(@QueryParam("merlId") String merlionId, @QueryParam("id") String id) {
        Preconditions.checkState(
                !StringUtils.isEmpty(merlionId) || !StringUtils.isEmpty(id)
                , "merlId or id must be set"
        );

        if(!StringUtils.isEmpty(merlionId) && StringUtils.isEmpty(id)) {
            return new Bindings("by merlId", new ArrayList<>());
        } else if (StringUtils.isEmpty(merlionId) && !StringUtils.isEmpty(id)) {
            return new Bindings("by id", new ArrayList<>());
        } else {
            return new Bindings("by both", new ArrayList<>());
        }
    }

    @GET @Path("/search")
    public Bond search(@QueryParam("merlId") String merlionId, @QueryParam("id") String id) {
        Preconditions.checkState(
                !StringUtils.isEmpty(merlionId) || !StringUtils.isEmpty(id)
                , "merlId or id must be set"
        );
        if(!StringUtils.isEmpty(merlionId) && StringUtils.isEmpty(id)) {
            return new Bond(merlionId, "some id");
        } else if (StringUtils.isEmpty(merlionId) && !StringUtils.isEmpty(id)) {
            return new Bond("some merlId", id);
        } else {
            return new Bond(merlionId, id);
        }
    }
}

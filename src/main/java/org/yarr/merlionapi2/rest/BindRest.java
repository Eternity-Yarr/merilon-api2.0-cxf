package org.yarr.merlionapi2.rest;

import com.google.common.base.Preconditions;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.directory.ItemsRepository;
import org.yarr.merlionapi2.model.Bindings;
import org.yarr.merlionapi2.model.Bond;
import org.yarr.merlionapi2.model.StockAndItem;
import org.yarr.merlionapi2.service.BindService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Path("/bind")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class BindRest
{
    private final BindService service;
    private final ItemsRepository itemsRepository;

    @Autowired
    public BindRest(BindService service, ItemsRepository itemsRepository) {
        this.service = service;
        this.itemsRepository = itemsRepository;
    }

    @GET @Path("/")
    public Bindings all() {
        return service.all();
    }

    @GET @Path("/cat/{catId}")
    public List<Bond> all(@PathParam("catId") String catalogId) {
        Preconditions.checkNotNull(catalogId, "catId parameter is required");
        Preconditions.checkState(!catalogId.isEmpty(), "catId shouldn't be empty");
        return service.get(catalogId);
    }


    @PUT @Path("/create")
    public Bindings bind(@QueryParam("catId") String catalogId, @QueryParam("merlId") String merlionId, @QueryParam("id") String id){
        Preconditions.checkNotNull(catalogId, "catId parameter is required");
        Preconditions.checkNotNull(merlionId, "merlId parameter is required");
        Preconditions.checkNotNull(id, "id parameter is required");
        Preconditions.checkState(!catalogId.isEmpty(), "catId parameter shouldn't be empty");
        Preconditions.checkState(!merlionId.isEmpty(), "merlId parameter shouldn't be empty");
        Preconditions.checkState(!id.isEmpty(), "id parameter shouldn't be empty");
        return service.bind(catalogId, new Bond(merlionId, catalogId, id));
    }

    @PUT @Path("/")
    public Bindings stage(@QueryParam("catId") String catalogId, @QueryParam("merlId") String merlionId) {
        Preconditions.checkNotNull(catalogId, "catId parameter is required");
        Preconditions.checkNotNull(merlionId, "merlId parameter is required");
        Preconditions.checkState(!catalogId.isEmpty(), "catId parameter shouldn't be empty");
        Preconditions.checkState(!merlionId.isEmpty(), "merlId parameter shouldn't be empty");
        return service.stage(catalogId, merlionId);
    }

    @DELETE @Path("/")
    public Bindings unbind(@QueryParam("merlId") String merlionId, @QueryParam("id") String id) {
        Preconditions.checkState(
                !StringUtils.isEmpty(merlionId) || !StringUtils.isEmpty(id)
                , "merlId or id must be set"
        );

        if(!StringUtils.isEmpty(merlionId) && StringUtils.isEmpty(id)) {
            return service.unbindByMerlId(merlionId);
        } else if (StringUtils.isEmpty(merlionId) && !StringUtils.isEmpty(id)) {
            return service.unbindById(id);
        } else {
            return service.unbind(merlionId, id);
        }
    }

    @GET @Path("/search")
    public Bond search(@QueryParam("merlId") String merlionId, @QueryParam("id") String id) {
        Preconditions.checkState(
                !StringUtils.isEmpty(merlionId) || !StringUtils.isEmpty(id)
                , "merlId or id must be set"
        );
        if(!StringUtils.isEmpty(merlionId) && StringUtils.isEmpty(id)) {
            return service.searchByMerlionId(merlionId);
        } else if (StringUtils.isEmpty(merlionId) && !StringUtils.isEmpty(id)) {
            return service.searchById(id);
        } else {
            return service.search(merlionId, id);
        }
    }

    @GET @Path("/check/{id}")
    public StockAndItem check(@PathParam("id") String id) throws ExecutionException {
        Bond b = service.searchById(id);
        if(b != null)
            return itemsRepository.get(b);
        else
            return null;
    }
}

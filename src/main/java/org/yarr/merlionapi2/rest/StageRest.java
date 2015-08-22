package org.yarr.merlionapi2.rest;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.Bond;
import org.yarr.merlionapi2.model.Item;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.CategoryService;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Collectors;

@Path("/stage")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class StageRest
{
    private final BindService bindService;
    private final CategoryService categoryService;
    private final TrackService trackService;

    @Autowired
    public StageRest(
            BindService bindService,
            CategoryService categoryService,
            TrackService trackService) {
        this.bindService = bindService;
        this.categoryService = categoryService;
        this.trackService = trackService;
    }

    @PUT
    @Path("/{merlionId}")
    public Bond stage(@PathParam("merlionId") String merlionId, Bond bond) {
        Preconditions.checkArgument(merlionId != null && !merlionId.isEmpty(), "merlionId must be supplied");
        for(String cn : trackService.all().nodes()) {
            if (categoryService.category(cn).items().get(merlionId) != null) {
                return bindService.stage(cn, merlionId);
            }
        }

        throw new IllegalArgumentException("No such item in tracked catalogs found");
    }

    @GET
    @Path("/")
    public List<Item> all() {
        return bindService.staging()
                .stream()
                .map(b -> categoryService.category(b.catId()).items().get(b.merlionId()))
                .collect(Collectors.toList());
    }

    @DELETE
    @Path("/{merlionId}")
    public void unstage(@PathParam("merlionId") String merlionId) {
        bindService.unbindByMerlId(merlionId);
    }
 /*   @PUT
    @Path("/{merlionId}")
    public Bond bind(@PathParam("merlionId") String merlionId, @QueryParam("id") String id) {
        Preconditions.checkArgument(merlionId != null && !merlionId.isEmpty(), "merlionId must be supplied");
        Preconditions.checkArgument(id != null && id.isEmpty(), "id must be supplied");
        //TODO: check that id is exists in our db
        for(CatalogNode cn : trackService.all().nodes().values()) {
            if (categoryService.category(cn).items().get(merlionId) != null) {
                return bindService.bind(cn.id(), new Bond(merlionId, cn.id(), id));
            }
        }

        throw new IllegalArgumentException("No such item in tracked catalogs found");
    }*/
}

package org.yarr.merlionapi2.rest;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.Bond;
import org.yarr.merlionapi2.model.Item;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.CategoryService;
import org.yarr.merlionapi2.service.ItemsService;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/stage")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class StageRest
{
    private final static Logger log = LoggerFactory.getLogger(StageRest.class);

    private final BindService bindService;
    private final CategoryService categoryService;
    private final TrackService trackService;
    private final ItemsService itemsService;

    @Autowired
    public StageRest(
            BindService bindService,
            CategoryService categoryService,
            TrackService trackService,
            ItemsService itemsService) {
        this.bindService = bindService;
        this.categoryService = categoryService;
        this.trackService = trackService;
        this.itemsService = itemsService;
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
        Set<String> itemIds = bindService.staging()
                .stream()
                .map(Bond::merlionId)
                .collect(Collectors.toSet());
        return itemsService.get(itemIds);
    }

    @DELETE
    @Path("/{merlionId}")
    public void unstage(@PathParam("merlionId") String merlionId) {
        bindService.unbindByMerlId(merlionId);
    }
}

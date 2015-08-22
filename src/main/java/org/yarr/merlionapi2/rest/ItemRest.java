package org.yarr.merlionapi2.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.Item;
import org.yarr.merlionapi2.service.CategoryService;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/item")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class ItemRest
{
    private final TrackService trackService;
    private final CategoryService categoryService;

    @Autowired
    public ItemRest(TrackService trackService, CategoryService categoryService) {
        this.trackService = trackService;
        this.categoryService = categoryService;
    }

    @GET
    @Path("/{merlionId}")
    public Item get(@PathParam("merlionId") String merlionId) {
        return trackService.all().nodes()
                .stream()
                .map(ts -> categoryService.category(ts).all())
                .reduce(
                    new ArrayList<>(),
                    (acc, list) -> {
                        acc.addAll(list);
                        return acc;
                    }
                ).stream()
                .filter(i -> i.id().equals(merlionId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No such item"));
    }
}

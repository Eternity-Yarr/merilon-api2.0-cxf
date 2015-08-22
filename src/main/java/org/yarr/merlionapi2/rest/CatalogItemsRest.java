package org.yarr.merlionapi2.rest;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.Category;
import org.yarr.merlionapi2.model.Stock;
import org.yarr.merlionapi2.model.StockAndItem;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.CatalogService;
import org.yarr.merlionapi2.service.CategoryService;
import org.yarr.merlionapi2.service.TrackService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/mlcatalogItems")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class CatalogItemsRest
{
    private final CategoryService categoryService;
    private final CatalogService catalogService;
    private final TrackService trackService;
    private final BindService bindService;

    @Autowired
    public CatalogItemsRest(
            CategoryService categoryService,
            CatalogService catalogService,
            TrackService trackService,
            BindService bindService) {
        this.categoryService = categoryService;
        this.catalogService = catalogService;
        this.trackService = trackService;
        this.bindService = bindService;
    }

    @GET
    @Path("/{catId}")
    public List<StockAndItem> consolidated(@PathParam("catId") String catId) {
        Preconditions.checkArgument(!catId.isEmpty(), "Category id shouldn't be empty");
        return getItems(catId);
    }

    @GET
    @Path("/")
    public List<StockAndItem> all() {
        List<StockAndItem> items = new ArrayList<>();
        for(String cn  : trackService.all().nodes())
            items.addAll(getItems(cn));
        return items;
    }

    private List<StockAndItem> getItems(String catId) {
        Category cat = categoryService.category(catalogService.get().nodes().get(catId));
        Stock stock = categoryService.stock(catalogService.get().nodes().get(catId));
        return cat.items().values().parallelStream()
                .map(i -> new StockAndItem(i.id(), i, stock.item(i.id())))
                .filter(i -> bindService.searchByMerlionId(i.id()) == null)
                .collect(Collectors.toList());
    }

}

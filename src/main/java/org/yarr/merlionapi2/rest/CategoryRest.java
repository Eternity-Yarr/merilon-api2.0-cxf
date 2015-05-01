package org.yarr.merlionapi2.rest;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.*;
import org.yarr.merlionapi2.service.CatalogService;
import org.yarr.merlionapi2.service.CategoryService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/mlcatalog")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class CategoryRest
{
    private final CategoryService categoryService;
    private final CatalogService catalogService;

    @Autowired
    public CategoryRest(CategoryService categoryService, CatalogService catalogService) {
        this.categoryService = categoryService;
        this.catalogService = catalogService;
    }

    @GET
    @Path("/")
    public Map<String, Integer> all() {
        return ImmutableMap.of(
                "Category entries", categoryService.caches().getKey().asMap().keySet().size(),
                "Stock entries", categoryService.caches().getValue().asMap().keySet().size()
        );
    }

    @GET
    @Path("/info/{catId}")
    public List<Item> items(@PathParam("catId") String catId) {
        return categoryService.category(catalogService.get().nodes().get(catId)).all();
    }

    @GET
    @Path("/stock/{catId}")
    public List<StockItem> stock(@PathParam("catId") String catId) {
        return categoryService.stock(catalogService.get().nodes().get(catId)).all();
    }

    @GET
    @Path("/both/{catId}")
    public List<StockAndItem> consolidated(@PathParam("catId") String catId) {
        Category cat = categoryService.category(catalogService.get().nodes().get(catId));
        Stock stock = categoryService.stock(catalogService.get().nodes().get(catId));
        return cat.items().values().parallelStream()
                .map(i -> new StockAndItem(i, stock.item(i.id())))
                .collect(
                        Collectors.toList()
                );
    }
}

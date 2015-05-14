package org.yarr.merlionapi2.rest;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.Bond;
import org.yarr.merlionapi2.service.BindService;
import org.yarr.merlionapi2.service.BitrixService;
import org.yarr.merlionapi2.service.CategoryService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/bind")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class BindRest
{
    private final static Logger log = LoggerFactory.getLogger(BindRest.class);
    private final BindService bindService;
    private final BitrixService bitrixService;
    private final CategoryService categoryService;

    @Autowired
    public BindRest(BindService bindService, BitrixService bitrixService, CategoryService categoryService) {
        this.bindService = bindService;
        this.bitrixService = bitrixService;
        this.categoryService = categoryService;
    }

    @PUT
    @Path("/{id}")
    public Bond bind(@PathParam("id") String id, Bond binding) {
        Preconditions.checkNotNull(categoryService.category(binding.catId()), "No such merlion catalog");
        Preconditions.checkNotNull(categoryService.category(binding.catId()).items().get(binding.merlionId()), "No such merlion item");
        //FIXME: kinda excessive
        Preconditions.checkArgument(id.equals(binding.id()), "bitrix id in path and in request body doesn't match");
        Bond bond = bitrixService
                .getById(id)
                .map(i -> new Bond(binding.merlionId(), binding.catId(), i.id()))
                .orElseThrow(() -> new IllegalArgumentException("No such item " + id));
        log.info("Added binding of {} to merlions {}", id, binding.merlionId());
        return bindService.bind(binding.catId(), bond);
    }

    @DELETE
    @Path("/{id}")
    public void unbind(@PathParam("id") String id) {
        log.info("Removing binding of {}", id);
        bindService.unbindById(id);
    }
}

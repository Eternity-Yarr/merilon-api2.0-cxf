package org.yarr.merlionapi2.rest;


import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yarr.merlionapi2.model.Item;
import org.yarr.merlionapi2.service.BitrixService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/bitrix")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class BitrixRest
{
    private final BitrixService bitrixService;

    @Autowired
    public BitrixRest(BitrixService bitrixService) {
        this.bitrixService = bitrixService;
    }

    @GET
    @Path("/{id}")
    public Pair<Item,Item> get(@PathParam("id") String id) {
        return new Pair<>(
                bitrixService.getByCode(id).orElse(null),
                bitrixService.getById(id).orElse(null)
        );
    }
}

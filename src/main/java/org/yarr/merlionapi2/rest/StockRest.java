package org.yarr.merlionapi2.rest;

import org.springframework.stereotype.Component;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/catalog")
@Produces(MediaType.APPLICATION_JSON)
@Component
public class StockRest
{
}

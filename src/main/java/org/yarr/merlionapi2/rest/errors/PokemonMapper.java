package org.yarr.merlionapi2.rest.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Catch em all!
 */
@Provider
public class PokemonMapper implements ExceptionMapper<Exception>
{
    private final Logger log = LoggerFactory.getLogger(PokemonMapper.class);

    @Override
    public Response toResponse(Exception exception)
    {
        log.warn("Got exception", exception);
        return Response.serverError().entity(exception).build();
    }
}

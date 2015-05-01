package org.yarr.merlionapi2.rest.errors;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Catch em all!
 */
@Provider
public class PokemonMapper implements ExceptionMapper<Exception>
{
    @Override
    public Response toResponse(Exception exception)
    {
        return Response.serverError().entity(exception).build();
    }
}

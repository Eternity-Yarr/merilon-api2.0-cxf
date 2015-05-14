package org.yarr.merlionapi2.rest.errors;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NPEMapper implements ExceptionMapper<NullPointerException>
{
    @Override
    public Response toResponse(NullPointerException exception)
    {
        return Response.serverError().entity(exception.getMessage()).type(MediaType.TEXT_PLAIN_TYPE).build();
    }
}

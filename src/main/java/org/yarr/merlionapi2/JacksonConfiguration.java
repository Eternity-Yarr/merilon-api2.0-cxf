package org.yarr.merlionapi2;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import javax.ws.rs.ext.ContextResolver;

public class JacksonConfiguration implements ContextResolver<ObjectMapper>
{
    @Override
    public ObjectMapper getContext(Class<?> type)
    {
        return new ObjectMapper()
                .configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
    }
}

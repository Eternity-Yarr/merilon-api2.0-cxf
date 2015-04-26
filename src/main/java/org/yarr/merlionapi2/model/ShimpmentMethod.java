package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ShimpmentMethod
{
    private final String code;
    private final String description;

    @JsonCreator
    public ShimpmentMethod(
            @JsonProperty("code") String code,
            @JsonProperty("description") String description) {
        this.code = code;
        this.description = description;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }

    @Override
    public String toString()
    {
        return code + ": " + description;
    }
}

package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class Bond
{
    private final String merlionId;
    private final String id;

    @JsonCreator
    public Bond(@JsonProperty("merlionId") String merlionId, @JsonProperty("id") String id) {
        this.merlionId = merlionId;
        this.id = id;
    }

    @JsonProperty
    public String merlionId()
    {
        return merlionId;
    }

    @JsonProperty
    public String id()
    {
        return id;
    }
}

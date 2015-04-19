package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class Bindings
{
    private final String catalogId;
    private final List<Bond> bonds;

    @JsonCreator
    public Bindings(@JsonProperty("catalogId") String catalogId, @JsonProperty("bonds") List<Bond> bonds) {
        this.catalogId = catalogId;
        this.bonds = bonds;
    }

    @JsonProperty
    public String catalogId()
    {
        return catalogId;
    }

    @JsonProperty
    public List<Bond> bonds()
    {
        return bonds;
    }
}

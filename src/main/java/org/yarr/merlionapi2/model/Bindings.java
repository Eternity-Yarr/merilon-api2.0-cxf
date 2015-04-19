package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;

public class Bindings
{
    private final Map<String, List<Bond>> bonds;

    @JsonCreator
    public Bindings(@JsonProperty("bonds") Map<String, List<Bond>> bonds) {
        this.bonds = bonds;
    }

    @JsonProperty
    public Map<String, List<Bond>> bonds()
    {
        return bonds;
    }

    public List<Bond> bonds(String catId) {
        return bonds.get(catId);
    }
}

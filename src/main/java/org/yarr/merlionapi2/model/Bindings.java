package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Bindings
{
    private final Map<String, Set<Bond>> bonds;

    @JsonCreator
    public Bindings(@JsonProperty("bonds") Map<String, Set<Bond>> bonds) {
        this.bonds = bonds;
    }

    @JsonProperty
    public Map<String, Set<Bond>> bonds()
    {
        return bonds;
    }

    public Set<Bond> bonds(String catId) {
        return bonds.get(catId);
    }
}

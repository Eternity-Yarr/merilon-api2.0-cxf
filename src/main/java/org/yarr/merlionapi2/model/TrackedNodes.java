package org.yarr.merlionapi2.model;

import com.google.common.collect.ImmutableList;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TrackedNodes
{
    private final Set<CatalogNode> trackedNodes = new HashSet<>();

    @JsonCreator
    public TrackedNodes(@JsonProperty("nodes") List<CatalogNode> trackedNodes) {
        this.trackedNodes.addAll(trackedNodes);
    }

    @JsonProperty
    public List<CatalogNode> nodes() {
        return ImmutableList.copyOf(trackedNodes);
    }
}

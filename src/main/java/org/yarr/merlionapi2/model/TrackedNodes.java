package org.yarr.merlionapi2.model;

import com.google.common.collect.ImmutableList;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;


public class TrackedNodes
{
    private final List<CatalogNode> trackedNodes = new ArrayList<>();

    public TrackedNodes(List<CatalogNode> trackedNodes) {
        this.trackedNodes.addAll(trackedNodes);
    }

    @JsonProperty
    public List<CatalogNode> nodes() {
        return ImmutableList.copyOf(trackedNodes);
    }
}

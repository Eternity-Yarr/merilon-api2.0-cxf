package org.yarr.merlionapi2.model;

import com.google.common.collect.ImmutableList;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;


public class TrackedNodes
{
    //FIXME: duplicates?
    private final List<CatalogNode> trackedNodes = new ArrayList<>();

    @JsonCreator
    public TrackedNodes(@JsonProperty("nodes") List<CatalogNode> trackedNodes) {
        this.trackedNodes.addAll(trackedNodes);
    }

    @JsonProperty
    public List<CatalogNode> nodes() {
        return ImmutableList.copyOf(trackedNodes);
    }
}

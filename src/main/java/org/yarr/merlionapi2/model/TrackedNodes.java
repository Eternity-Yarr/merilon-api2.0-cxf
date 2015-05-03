package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.Map;


public class TrackedNodes
{
    private final Map<String, CatalogNode> trackedNodes = new HashMap<>();

    @JsonCreator
    public TrackedNodes(@JsonProperty("nodes") Map<String, CatalogNode> trackedNodes) {
        this.trackedNodes.putAll(trackedNodes);
    }

    @JsonProperty
    public Map<String, CatalogNode> nodes() {
        return trackedNodes;
    }
}

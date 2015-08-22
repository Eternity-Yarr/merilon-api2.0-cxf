package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashSet;
import java.util.Set;


public class TrackedNodes
{
    private final Set<String> trackedNodes = new HashSet<>();

    @JsonCreator
    public TrackedNodes(@JsonProperty("nodes") Set<String> trackedNodes) {
        this.trackedNodes.addAll(trackedNodes);
    }

    @JsonProperty
    public Set<String> nodes() {
        return trackedNodes;
    }
}

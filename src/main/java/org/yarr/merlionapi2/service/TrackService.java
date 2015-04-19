package org.yarr.merlionapi2.service;

import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.model.TrackedNodes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrackService
{
    private TrackedNodes trackedNodes = new TrackedNodes(new ArrayList<>());

    public TrackedNodes all() {
        return trackedNodes;
    }

    public TrackedNodes track(CatalogNode node) {
        List<CatalogNode> nodes = new ArrayList<>(trackedNodes.nodes());
        nodes.add(node);
        trackedNodes = new TrackedNodes(nodes);
        return trackedNodes;
    }

    public TrackedNodes discard(CatalogNode node) {
        List<CatalogNode> nodes = trackedNodes
                .nodes()
                .stream()
                .filter(x -> !x.id().equals(node.id()))
                .collect(Collectors.toList());
        trackedNodes = new TrackedNodes(nodes);
        return trackedNodes;
    }

    public void refresh() {}

    public static TrackService i() {
        return Lazy.service;
    }

    private static class Lazy {
        public static final TrackService service = new TrackService();
    }
}

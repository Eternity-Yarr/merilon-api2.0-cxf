package org.yarr.merlionapi2.service;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.model.TrackedNodes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrackService
{
    private final Logger log = LoggerFactory.getLogger(TrackService.class);
    private final static String STORAGE = "./data/tracked_catalogs.json";
    private final static ObjectMapper mapper = new ObjectMapper();

    private TrackedNodes trackedNodes = get();

    public TrackedNodes all() {
        return get();
    }

    private TrackedNodes get() {
        try
        {
            TrackedNodes nodes = mapper.readValue(new File(STORAGE), TrackedNodes.class);
            log.debug("Read {} nodes from '{}' file", nodes.nodes().size(), STORAGE);
            trackedNodes = nodes;
        } catch (Exception e) {
            log.error("Cannot read/parse '{}' file - '{}', no tracking info available", STORAGE, e.getMessage());
            log.debug("Exception trace", e);
            if (trackedNodes == null) {
                log.debug("Replacing with empty list instead");
                trackedNodes = new TrackedNodes(new ArrayList<>());
            }
        }
        return trackedNodes;
    }

    private TrackedNodes set(List<CatalogNode> nodes) {
        try
        {
            TrackedNodes newTrackedNodes = new TrackedNodes(nodes);
            String json = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(newTrackedNodes);
            FileUtils.writeStringToFile(new File(STORAGE), json);
            log.info("Wrote {} nodes to file '{}'", newTrackedNodes.nodes().size(), STORAGE);
            trackedNodes = newTrackedNodes;

        } catch (Exception e) {
            log.error(
                "Cannot write tracking info to file '{}' - '{}', changes are not saved",
                    STORAGE, e.getMessage());
            log.debug("Exception trace", e);
        }

        return trackedNodes;
    }

    public TrackedNodes track(CatalogNode node) {
        List<CatalogNode> nodes = new ArrayList<>(trackedNodes.nodes());
        nodes.add(node);
        trackedNodes = set(nodes);
        return trackedNodes;
    }

    public TrackedNodes discard(CatalogNode node) {
        List<CatalogNode> nodes = trackedNodes
                .nodes()
                .stream()
                .filter(x -> !x.id().equals(node.id()))
                .collect(Collectors.toList());
        trackedNodes = set(nodes);
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

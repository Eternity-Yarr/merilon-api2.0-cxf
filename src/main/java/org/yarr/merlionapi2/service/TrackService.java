package org.yarr.merlionapi2.service;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.yarr.merlionapi2.model.CatalogNode;
import org.yarr.merlionapi2.model.TrackedNodes;

import java.io.File;
import java.util.HashSet;

@Service
public class TrackService
{
    private final Logger log = LoggerFactory.getLogger(TrackService.class);
    private final static String STORAGE = "./data/tracked_catalog_ids.json";
    private final static ObjectMapper mapper = new ObjectMapper();

    private TrackedNodes trackedNodes = get();

    public TrackedNodes all() {
        return get();
    }

    private synchronized TrackedNodes get() {
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
                trackedNodes = new TrackedNodes(new HashSet<>());
            }
        }
        return trackedNodes;
    }

    private synchronized TrackedNodes update() {
        try
        {
            String json = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(trackedNodes);
            FileUtils.writeStringToFile(new File(STORAGE), json);
            log.info("Wrote {} nodes to file '{}'", trackedNodes.nodes().size(), STORAGE);
        } catch (Exception e) {
            log.error(
                "Cannot write tracking info to file '{}' - '{}', changes are not saved",
                    STORAGE, e.getMessage());
            log.debug("Exception trace", e);
        }

        return trackedNodes;
    }

    public boolean tracked(String nodeId) {
        return trackedNodes.nodes().contains(nodeId);
    }

    public CatalogNode track(CatalogNode node) {
        trackedNodes.nodes().add(node.id());
        update();
        return node;
    }

    public TrackedNodes discard(String id) {
        trackedNodes.nodes().remove(id);
        update();
        return trackedNodes;
    }
}

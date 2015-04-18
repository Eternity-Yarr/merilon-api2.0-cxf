package org.yarr.merlionapi2.directory;

import com.google.common.collect.ImmutableMap;
import org.yarr.merlionapi2.model.CatalogNode;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Catalog
{
    private final Map<String, CatalogNode> nodes;

    public Catalog(Map<String, CatalogNode> nodes) {
        this.nodes = nodes;
    }

    public Map<String, CatalogNode> nodes() {
        return ImmutableMap.copyOf(nodes);
    }

    public List<CatalogNode> childsOf(CatalogNode parent) {
        return nodes
                .values()
                .parallelStream()
                .filter(
                    node -> !node.topLevel() &&
                    node.parentId().equals(parent.id()))
                .collect(Collectors.toList());
    }

    public boolean hasChilds(CatalogNode parent) {
        return nodes
                .values()
                .parallelStream()
                .anyMatch(
                        node -> !node.topLevel() &&
                        node.parentId().equals(parent.id())
                );
    }
}

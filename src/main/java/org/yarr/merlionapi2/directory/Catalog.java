package org.yarr.merlionapi2.directory;

import com.google.common.collect.ImmutableMap;
import org.eclipse.jetty.util.ArrayQueue;
import org.yarr.merlionapi2.model.CatalogNode;

import java.util.*;
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

    public String canonicalName(CatalogNode node) {
        List<CatalogNode> chain = new ArrayList<>();
        chain.add(node);
        CatalogNode ctx = node;
        while(ctx != null && !ctx.topLevel()) {
            CatalogNode parent = nodes.get(ctx.parentId());
            chain.add(parent);
            ctx = parent;
        }
        Collections.reverse(chain);
        return node.id() +
            chain
                .stream()
                .map(CatalogNode::name)
                .reduce("", (x, y) -> x + " -> " + y);
    }

    @Override
    public String toString()
    {
        return "Catalog with " + nodes.size() +
                " nodes";
    }
}

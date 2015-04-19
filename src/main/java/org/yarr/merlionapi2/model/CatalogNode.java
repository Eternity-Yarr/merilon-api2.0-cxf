package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonProperty;

public class CatalogNode
{
    private final String parentId;
    private final String name;
    private final String id;

    public CatalogNode(String parentId, String name, String id)
    {
        this.parentId = parentId;
        this.name = name;
        this.id = id;
    }

    @JsonProperty
    public String parentId()
    {
        return parentId;
    }

    @JsonProperty
    public String name()
    {
        return name;
    }

    @JsonProperty
    public String id()
    {
        return id;
    }

    public boolean topLevel() {
        return parentId == null;
    }

    @Override
    public String toString()
    {
        return String.format("[%s] %s", id, name);
    }
}

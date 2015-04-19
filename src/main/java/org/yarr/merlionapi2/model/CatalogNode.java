package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.yarr.merlionapi2.directory.Catalog;

public class CatalogNode
{
    private final String parentId;
    private final String name;
    private final String id;

    @JsonCreator
    public CatalogNode(
            @JsonProperty("parentId") String parentId,
            @JsonProperty("name") String name,
            @JsonProperty("id") String id)
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

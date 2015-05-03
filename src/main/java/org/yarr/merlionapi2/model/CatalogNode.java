package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import javax.validation.constraints.NotNull;

@JsonPropertyOrder({"id", "parentId", "name", "tracked"})
public class CatalogNode implements Comparable<CatalogNode>
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogNode that = (CatalogNode) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(@NotNull CatalogNode o)
    {
        return this.id().compareTo(o.id());
    }
}

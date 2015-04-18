package org.yarr.merlionapi2.model;

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

    public String parentId()
    {
        return parentId;
    }

    public String name()
    {
        return name;
    }

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

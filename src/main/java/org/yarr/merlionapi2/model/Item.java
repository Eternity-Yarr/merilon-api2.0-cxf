package org.yarr.merlionapi2.model;

public class Item
{
    private final String vendorCode;
    private final String id;
    private final String name;
    private final String brand;

    public Item(String vendorCode, String id, String name, String brand)
    {
        this.vendorCode = vendorCode;
        this.id = id;
        this.name = name;
        this.brand = brand;
    }

    public String vendorCode()
    {
        return vendorCode;
    }

    public String id()
    {
        return id;
    }

    public String name()
    {
        return name;
    }

    public String brand()
    {
        return brand;
    }
}

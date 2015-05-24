package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder({"id", "catId", "vendorCode", "brand", "name"})
public class Item
{
    private final String id;
    private final String catId;
    private final String vendorCode;
    private final String name;
    private final String brand;

    @JsonCreator
    public Item(
            @JsonProperty("id") String id,
            @JsonProperty("catId") String catId,
            @JsonProperty("vendorCode") String vendorCode,
            @JsonProperty("name") String name,
            @JsonProperty("brand") String brand)
    {
        this.id = id;
        this.catId = catId;
        this.vendorCode = vendorCode;
        this.name = name;
        this.brand = brand;
    }

    @JsonProperty
    public String id()
    {
        return id;
    }

    @JsonProperty
    public String catId()
    {
        return catId;
    }

    @JsonProperty
    public String vendorCode()
    {
        return vendorCode;
    }

    @JsonProperty
    public String name()
    {
        return name;
    }

    @JsonProperty
    public String brand()
    {
        return brand;
    }

    @Override
    public String toString()
    {
        return "Item{" +
                "vendorCode='" + vendorCode + '\'' +
                ", catId='" + catId + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}

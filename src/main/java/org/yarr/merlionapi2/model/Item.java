package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder({"id", "vendorCode", "brand", "name"})
public class Item
{
    private final String vendorCode;
    private final String id;
    private final String name;
    private final String brand;

    @JsonCreator
    public Item(
            @JsonProperty("vendorCode") String vendorCode,
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("brand") String brand)
    {
        this.vendorCode = vendorCode;
        this.id = id;
        this.name = name;
        this.brand = brand;
    }
    @JsonProperty

    public String id()
    {
        return id;
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
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}

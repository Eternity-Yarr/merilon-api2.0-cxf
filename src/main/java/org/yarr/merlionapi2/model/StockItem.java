package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder({"id", "price", "available"})
public class StockItem
{
    private final double price;
    private final int available;
    private final String id;

    @JsonCreator
    public StockItem(float price, int available, String id)
    {
        this.price = price;
        this.available = available;
        this.id = id;
    }

    @JsonProperty
    public double price() {
        return price;
    }

    @JsonProperty
    public int available() {
        return available;
    }

    @JsonProperty
    public String id() {
        return id;
    }

    @Override
    public String toString()
    {
        return "StockItem{" +
                "price=" + price +
                ", available=" + available +
                ", id='" + id + '\'' +
                '}';
    }
}


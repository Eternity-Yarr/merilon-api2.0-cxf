package org.yarr.merlionapi2.model;

public class StockItem
{
    private final float price;
    private final int available;
    private final String id;

    public StockItem(float price, int available, String id)
    {
        this.price = price;
        this.available = available;
        this.id = id;
    }

    public float price() {
        return price;
    }

    public int available() {
        return available;
    }

    public String id() {
        return id;
    }
}


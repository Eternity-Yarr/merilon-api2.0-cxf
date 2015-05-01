package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class StockAndItem
{
    private final String id;
    private final Item item;
    private final StockItem stock;

    @JsonCreator
    public StockAndItem(String id, Item item, StockItem stock) {
        this.id = id;
        this.item = item;
        this.stock = stock;
    }

    @JsonProperty
    public Item item(){
        return item;
    }

    @JsonProperty
    public StockItem stock() {
        return stock;
    }

    public String id() {
        return id;
    }
}

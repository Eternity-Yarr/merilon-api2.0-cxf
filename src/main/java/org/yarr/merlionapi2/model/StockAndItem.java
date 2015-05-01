package org.yarr.merlionapi2.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class StockAndItem
{
    private final Item item;
    private final StockItem stock;

    @JsonCreator
    public StockAndItem(Item item, StockItem stock) {
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
}

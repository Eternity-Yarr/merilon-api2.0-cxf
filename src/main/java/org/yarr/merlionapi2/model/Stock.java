package org.yarr.merlionapi2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Stock
{
    private final Map<String, StockItem> stocks;
    public Stock(Map<String, StockItem> stocks)
    {
        this.stocks = stocks;
    }

    public StockItem item(String itemId) {
        return stocks.get(itemId);
    }

    public List<StockItem> all() {
        return new ArrayList<>(stocks.values());
    }

    public Map<String, StockItem> stocks() {
        return stocks;
    }
}

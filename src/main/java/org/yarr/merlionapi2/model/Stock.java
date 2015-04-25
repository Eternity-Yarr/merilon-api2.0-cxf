package org.yarr.merlionapi2.model;

import java.util.Map;

public class Stock
{
    private final Map<String, StockItem> stocks;
    public Stock(Map<String, StockItem> stocks)
    {
        this.stocks = stocks;
    }
}

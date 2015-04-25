package org.yarr.merlionapi2.model;

import java.util.Map;

public class Category
{
    private final Map<String, Item> items;

    public Category(Map<String,Item> items) {
        this.items = items;
    }
}

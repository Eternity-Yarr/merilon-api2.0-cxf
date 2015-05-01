package org.yarr.merlionapi2.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Category
{
    private final Map<String, Item> items;

    public Category(Map<String,Item> items) {
        this.items = items;
    }

    public List<Item> all() {
        return new ArrayList<>(items.values());
    }

    public Map<String, Item> items() {
        return items;
    }
}

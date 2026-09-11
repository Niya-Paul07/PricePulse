package com.pricepulse.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a trackable product (e.g. "Rice 1kg").
 * Core class from the system design (docs/design.md).
 */
public class Item {
    private int id;
    private String name;
    private String category;
    private String unit;

    public Item(int id, String name, String category, String unit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    /**
     * Placeholder used by Trend Analysis / Price-Change Detection teammates.
     * Filters the master entry list down to entries for this item.
     */
    public List<PriceEntry> getPriceHistory(List<PriceEntry> allEntries) {
        List<PriceEntry> history = new ArrayList<>();
        for (PriceEntry entry : allEntries) {
            if (entry.getItem().getId() == this.id) {
                history.add(entry);
            }
        }
        return history;
    }

    @Override
    public String toString() {
        return name + " (" + unit + ", " + category + ")";
    }
}

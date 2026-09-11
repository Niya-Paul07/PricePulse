package com.pricepulse.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shop that sells items at various prices.
 * Core class from the system design (docs/design.md).
 */
public class Shop {
    private int id;
    private String name;
    private String location;

    public Shop(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    /**
     * Placeholder used by Cheapest Shop Lookup / Price Entry CRUD teammates.
     * Filters the master entry list down to entries from this shop.
     */
    public List<PriceEntry> getPriceEntries(List<PriceEntry> allEntries) {
        List<PriceEntry> result = new ArrayList<>();
        for (PriceEntry entry : allEntries) {
            if (entry.getShop().getId() == this.id) {
                result.add(entry);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}

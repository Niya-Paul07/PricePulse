
package com.pricepulse;

import java.util.ArrayList;
import java.util.List;
import com.pricepulse.model.PriceEntry;
public class Item {

    private int id;
    private String name;
    private String category;
    private String unit;

    public Item() {
    }

    public Item(int id, String name, String category, String unit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
    }

    public Item(String name, String category, String unit) {
        this.name = name;
        this.category = category;
        this.unit = unit;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    // Price history will be handled by PriceEntry/PriceTrackerService
    public List<PriceEntry> getPriceHistory() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
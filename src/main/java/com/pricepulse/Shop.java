package com.pricepulse;

import java.util.ArrayList;
import java.util.List;
import com.pricepulse.model.PriceEntry;

public class Shop {

    private int id;
    private String name;
    private String location;

    public Shop() {
    }

    public Shop(int id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Shop(String name, String location) {
        this.name = name;
        this.location = location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Price entries will be handled by PriceEntry/PriceTrackerService
    public List<PriceEntry> getPriceEntries() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Shop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
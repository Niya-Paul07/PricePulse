package com.pricepulse;

import com.pricepulse.model.PriceEntry;
import java.util.ArrayList;
import java.util.List;
import com.pricepulse.model.PriceEntry;

public class Item {

    private int id;
    private String name;
    private String category;
    private String unit;
    private double price;
    private String shopName;

    public Item() {
        this.shopName = "";
    }

    public Item(int id, String name, String category, String unit) {
        this(id, name, category, unit, 0.0, "");
    }

    public Item(String name, String category, String unit) {
        this(0, name, category, unit, 0.0, "");
    }

    public Item(int id, String name, String category,
                String unit, double price) {
        this(id, name, category, unit, price, "");
    }

    public Item(String name, String category,
                String unit, double price) {
        this(0, name, category, unit, price, "");
    }

    public Item(String name, String category, String unit,
                double price, String shopName) {
        this(0, name, category, unit, price, shopName);
    }

    public Item(int id, String name, String category,
                String unit, double price, String shopName) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.price = price;
        this.shopName = shopName == null ? "" : shopName;
    }

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName == null ? "" : shopName;
    }

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
                ", price=" + price +
                ", shopName='" + shopName + '\'' +
                '}';
    }
}
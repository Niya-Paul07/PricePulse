package com.pricepulse.model;

import java.time.LocalDate;

/**
 * Represents a single reported price of an Item at a Shop by a User.
 * Core class from the system design (docs/design.md).
 *
 * Design rule: item, shop, and user are required — never null.
 */
public class PriceEntry {
    private int id;
    private Item item;
    private Shop shop;
    private User user;
    private double price;
    private LocalDate date;

    public PriceEntry(int id, Item item, Shop shop, User user, double price, LocalDate date) {
        this.id = id;
        this.item = item;
        this.shop = shop;
        this.user = user;
        this.price = price;
        this.date = date;
    }

    public int getId() { return id; }
    public Item getItem() { return item; }
    public Shop getShop() { return shop; }
    public User getUser() { return user; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    /**
     * Enforces the design doc's "no nulls" rule for item/shop/user,
     * plus a sane non-negative price check.
     */
    public boolean isValid() {
        return item != null && shop != null && user != null && price >= 0;
    }

    @Override
    public String toString() {
        return item.getName() + " @ " + shop.getName() + ": Rs." + price + " (" + date + ")";
    }
}

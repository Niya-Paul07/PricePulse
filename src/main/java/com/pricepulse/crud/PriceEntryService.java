
package com.pricepulse.crud;

import com.pricepulse.model.Item;
import com.pricepulse.model.Shop;
import com.pricepulse.model.User;
import com.pricepulse.model.PriceEntry;
import com.pricepulse.storage.PriceEntryStorage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceEntryService {

    private final List<PriceEntry> entries = new ArrayList<>();
    private final List<Item> items;
    private final List<Shop> shops;
    private final List<User> users;
    private final PriceEntryStorage storage;

    private int nextId = 1;

    public PriceEntryService(
            List<Item> items,
            List<Shop> shops,
            List<User> users) {

        if (items == null || shops == null || users == null) {
            throw new IllegalArgumentException(
                    "Item, shop, and user lists are required.");
        }

        this.items = items;
        this.shops = shops;
        this.users = users;
        this.storage = new PriceEntryStorage();

        try {
            entries.addAll(
                    storage.loadEntries(this.items, this.shops, this.users));

            for (PriceEntry entry : entries) {
                if (entry.getId() >= nextId) {
                    nextId = entry.getId() + 1;
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not load saved price entries.", ex);
        }
    }

    // CREATE
    public PriceEntry submitPriceEntry(
            int itemId, int shopId, int userId,
            double price, LocalDate date) {

        Item item = findItem(itemId);
        Shop shop = findShop(shopId);
        User user = findUser(userId);

        if (item == null || shop == null || user == null) {
            throw new IllegalArgumentException(
                    "The selected item, shop, or user does not exist.");
        }

        validatePriceAndDate(price, date);

        PriceEntry entry = new PriceEntry(
                nextId, item, shop, user, price, date);

        entries.add(entry);

        try {
            saveEntries();
            nextId++;
            return entry;
        } catch (IllegalStateException ex) {
            entries.remove(entry);
            throw ex;
        }
    }

    // READ - all entries
    public List<PriceEntry> getAllEntries() {
        return new ArrayList<>(entries);
    }

    // READ - one entry
    public PriceEntry getEntryById(int id) {
        for (PriceEntry entry : entries) {
            if (entry.getId() == id) {
                return entry;
            }
        }
        return null;
    }

    // UPDATE - change price and date
    public boolean updatePriceEntry(
            int id, double newPrice, LocalDate newDate) {

        PriceEntry entry = getEntryById(id);

        if (entry == null) {
            return false;
        }

        validatePriceAndDate(newPrice, newDate);

        double oldPrice = entry.getPrice();
        LocalDate oldDate = entry.getDate();

        entry.setPrice(newPrice);
        entry.setDate(newDate);

        try {
            saveEntries();
            return true;
        } catch (IllegalStateException ex) {
            entry.setPrice(oldPrice);
            entry.setDate(oldDate);
            throw ex;
        }
    }

    // DELETE
    public boolean deletePriceEntry(int id) {
        PriceEntry entry = getEntryById(id);

        if (entry == null) {
            return false;
        }

        entries.remove(entry);

        try {
            saveEntries();
            return true;
        } catch (IllegalStateException ex) {
            entries.add(entry);
            throw ex;
        }
    }

    private void saveEntries() {
        try {
            storage.saveEntries(entries);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not save price entries to CSV.", ex);
        }
    }

    // VALIDATION
    private void validatePriceAndDate(
            double price, LocalDate date) {

        if (!Double.isFinite(price) || price <= 0) {
            throw new IllegalArgumentException(
                    "Price must be a finite number greater than zero.");
        }

        if (date == null) {
            throw new IllegalArgumentException(
                    "A valid date is required.");
        }
    }

    // FIND REFERENCED OBJECTS
    private Item findItem(int id) {
        for (Item item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    private Shop findShop(int id) {
        for (Shop shop : shops) {
            if (shop.getId() == id) {
                return shop;
            }
        }
        return null;
    }

    private User findUser(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}

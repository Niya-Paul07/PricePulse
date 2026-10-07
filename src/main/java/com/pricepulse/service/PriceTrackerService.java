package com.pricepulse.service;

import com.pricepulse.analysis.TrendAnalysisService;
import com.pricepulse.model.Item;
import com.pricepulse.model.PriceEntry;
import com.pricepulse.model.Shop;
import com.pricepulse.model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Central data holder for items, shops and price entries (see docs/design.md).
 *
 * Storage rule from the design doc: everything lives in memory at runtime,
 * loaded once at startup (loadData) and saved on exit (saveData).
 * CRUD panels and analyzers should all go through this one class.
 *
 * Users are owned by AuthService; pass authService.getAllUsers() in, and call
 * authService.loadData() BEFORE loadData() here so entries can be linked to users.
 *
 * CSV files (inside dataDir):
 *   items.csv         id,name,category,unit
 *   shops.csv         id,name,location
 *   price_entries.csv id,itemId,shopId,userId,price,date(yyyy-MM-dd)
 */
public class PriceTrackerService {

    private final List<Item> items = new ArrayList<>();
    private final List<Shop> shops = new ArrayList<>();
    private final List<PriceEntry> entries = new ArrayList<>();
    private final List<User> users;
    private final String dataDir;

    // Composition (not inheritance): the service uses the analyzers.
    // It shares the live 'entries' list, so analysis always sees current data.
    private final TrendAnalysisService analysisService;

    private int nextItemId = 1;
    private int nextShopId = 1;
    private int nextEntryId = 1;

    public PriceTrackerService(String dataDir, List<User> users) {
        this.dataDir = dataDir;
        this.users = users;
        this.analysisService = new TrendAnalysisService(entries);
    }

    // ---------- read access (read-only views; change data via the methods below) ----------

    public List<Item> getItems() { return Collections.unmodifiableList(items); }
    public List<Shop> getShops() { return Collections.unmodifiableList(shops); }
    public List<PriceEntry> getEntries() { return Collections.unmodifiableList(entries); }
    public TrendAnalysisService getAnalysisService() { return analysisService; }

    public Item findItemById(int id) {
        for (Item i : items) if (i.getId() == id) return i;
        return null;
    }

    public Shop findShopById(int id) {
        for (Shop s : shops) if (s.getId() == id) return s;
        return null;
    }

    private User findUserById(int id) {
        for (User u : users) if (u.getId() == id) return u;
        return null;
    }

    // ---------- create / delete (update = use the setters on Item, Shop, PriceEntry) ----------

    public Item addItem(String name, String category, String unit) {
        Item item = new Item(nextItemId++, name, category, unit);
        items.add(item);
        return item;
    }

    public Shop addShop(String name, String location) {
        Shop shop = new Shop(nextShopId++, name, location);
        shops.add(shop);
        return shop;
    }

    /** Returns the new entry, or null if it breaks the design rules (null item/shop/user/date, negative price). */
    public PriceEntry addPriceEntry(Item item, Shop shop, User user, double price, LocalDate date) {
        PriceEntry entry = new PriceEntry(nextEntryId, item, shop, user, price, date);
        if (date == null || !entry.isValid()) {
            return null;
        }
        nextEntryId++;
        entries.add(entry);
        return entry;
    }

    /** Also removes every price entry for this item (an entry can't exist without its item). */
    public void removeItem(Item item) {
        entries.removeIf(e -> e.getItem().getId() == item.getId());
        items.remove(item);
    }

    /** Also removes every price entry from this shop. */
    public void removeShop(Shop shop) {
        entries.removeIf(e -> e.getShop().getId() == shop.getId());
        shops.remove(shop);
    }

    public void removeEntry(PriceEntry entry) {
        entries.remove(entry);
    }

    // ---------- cheapest shop lookup ----------

    /**
     * Compares each shop's most recent price for the item and returns the cheapest of those,
     * or null if the item has no entries.
     */
    public PriceEntry getCheapestEntry(Item item) {
        if (item == null) return null;

        Map<Integer, PriceEntry> latestPerShop = new HashMap<>();
        for (PriceEntry e : entries) {
            if (e.getItem().getId() != item.getId()) continue;
            PriceEntry current = latestPerShop.get(e.getShop().getId());
            if (current == null || !e.getDate().isBefore(current.getDate())) {
                latestPerShop.put(e.getShop().getId(), e);
            }
        }

        PriceEntry cheapest = null;
        for (PriceEntry e : latestPerShop.values()) {
            if (cheapest == null
                    || e.getPrice() < cheapest.getPrice()
                    || (e.getPrice() == cheapest.getPrice()
                        && e.getShop().getId() < cheapest.getShop().getId())) {
                cheapest = e;
            }
        }
        return cheapest;
    }

    public Shop getCheapestShop(Item item) {
        PriceEntry e = getCheapestEntry(item);
        return e == null ? null : e.getShop();
    }

    // ---------- persistence ----------

    public void loadData() {
        items.clear();
        shops.clear();
        entries.clear();
        nextItemId = 1;
        nextShopId = 1;
        nextEntryId = 1;

        for (String[] r : readRows("items.csv", 4)) {
            try {
                int id = Integer.parseInt(r[0].trim());
                items.add(new Item(id, r[1], r[2], r[3]));
                nextItemId = Math.max(nextItemId, id + 1);
            } catch (NumberFormatException e) {
                System.out.println("Skipping bad item row: " + String.join(",", r));
            }
        }

        for (String[] r : readRows("shops.csv", 3)) {
            try {
                int id = Integer.parseInt(r[0].trim());
                shops.add(new Shop(id, r[1], r[2]));
                nextShopId = Math.max(nextShopId, id + 1);
            } catch (NumberFormatException e) {
                System.out.println("Skipping bad shop row: " + String.join(",", r));
            }
        }

        for (String[] r : readRows("price_entries.csv", 6)) {
            try {
                int id = Integer.parseInt(r[0].trim());
                Item item = findItemById(Integer.parseInt(r[1].trim()));
                Shop shop = findShopById(Integer.parseInt(r[2].trim()));
                User user = findUserById(Integer.parseInt(r[3].trim()));
                double price = Double.parseDouble(r[4].trim());
                LocalDate date = LocalDate.parse(r[5].trim());

                PriceEntry entry = new PriceEntry(id, item, shop, user, price, date);
                if (!entry.isValid()) {
                    System.out.println("Skipping entry with missing item/shop/user: " + String.join(",", r));
                    continue;
                }
                entries.add(entry);
                nextEntryId = Math.max(nextEntryId, id + 1);
            } catch (NumberFormatException | DateTimeParseException e) {
                System.out.println("Skipping bad price entry row: " + String.join(",", r));
            }
        }
    }

    public void saveData() {
        new File(dataDir).mkdirs();

        try (PrintWriter w = new PrintWriter(new FileWriter(new File(dataDir, "items.csv")))) {
            for (Item i : items) {
                w.println(i.getId() + "," + clean(i.getName()) + "," + clean(i.getCategory()) + "," + clean(i.getUnit()));
            }
        } catch (IOException e) {
            System.out.println("Could not save items: " + e.getMessage());
        }

        try (PrintWriter w = new PrintWriter(new FileWriter(new File(dataDir, "shops.csv")))) {
            for (Shop s : shops) {
                w.println(s.getId() + "," + clean(s.getName()) + "," + clean(s.getLocation()));
            }
        } catch (IOException e) {
            System.out.println("Could not save shops: " + e.getMessage());
        }

        try (PrintWriter w = new PrintWriter(new FileWriter(new File(dataDir, "price_entries.csv")))) {
            for (PriceEntry e : entries) {
                w.println(e.getId() + "," + e.getItem().getId() + "," + e.getShop().getId() + ","
                        + e.getUser().getId() + "," + e.getPrice() + "," + e.getDate());
            }
        } catch (IOException e) {
            System.out.println("Could not save price entries: " + e.getMessage());
        }
    }

    /** Commas and line breaks would corrupt the CSV, so replace them with spaces. */
    private String clean(String s) {
        return s == null ? "" : s.replace(",", " ").replace("\n", " ").replace("\r", " ");
    }

    private List<String[]> readRows(String fileName, int columns) {
        List<String[]> rows = new ArrayList<>();
        File file = new File(dataDir, fileName);
        if (!file.exists()) return rows;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length != columns) {
                    System.out.println("Skipping malformed line in " + fileName + ": " + line);
                    continue;
                }
                rows.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Could not load " + fileName + ": " + e.getMessage());
        }
        return rows;
    }
}

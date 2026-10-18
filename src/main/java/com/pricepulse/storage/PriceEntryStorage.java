package com.pricepulse.storage;

import com.pricepulse.model.Item;
import com.pricepulse.model.Shop;
import com.pricepulse.model.User;
import com.pricepulse.model.PriceEntry;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceEntryStorage {

    private final Path filePath;

    public PriceEntryStorage() {
        this(Paths.get("data", "price_entries.csv"));
    }

    public PriceEntryStorage(Path filePath) {
        this.filePath = filePath;
    }

    public void saveEntries(List<PriceEntry> entries) throws IOException {
        Path parent = filePath.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                filePath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            writer.write("id,itemId,shopId,userId,price,date");
            writer.newLine();

            for (PriceEntry entry : entries) {
                writer.write(
                    entry.getId() + ","
                    + entry.getItem().getId() + ","
                    + entry.getShop().getId() + ","
                    + entry.getUser().getId() + ","
                    + entry.getPrice() + ","
                    + entry.getDate()
                );
                writer.newLine();
            }
        }
    }

    public List<PriceEntry> loadEntries(
            List<Item> items,
            List<Shop> shops,
            List<User> users) throws IOException {

        List<PriceEntry> entries = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return entries;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                filePath, StandardCharsets.UTF_8)) {

            String line = reader.readLine(); // Skip CSV header
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) {
                    continue;
                }

                String[] columns = line.split(",", -1);

                if (columns.length != 6) {
                    throw new IOException(
                        "Invalid CSV row at line " + lineNumber);
                }

                try {
                    int id = Integer.parseInt(columns[0].trim());
                    int itemId = Integer.parseInt(columns[1].trim());
                    int shopId = Integer.parseInt(columns[2].trim());
                    int userId = Integer.parseInt(columns[3].trim());
                    double price = Double.parseDouble(columns[4].trim());
                    LocalDate date = LocalDate.parse(columns[5].trim());

                    Item item = findItem(items, itemId);
                    Shop shop = findShop(shops, shopId);
                    User user = findUser(users, userId);

                    if (item == null || shop == null || user == null) {
                        throw new IOException(
                            "Missing item, shop, or user reference at line "
                            + lineNumber);
                    }

                    PriceEntry entry = new PriceEntry(
                        id, item, shop, user, price, date);

                    if (!entry.isValid()) {
                        throw new IOException(
                            "Invalid price entry at line " + lineNumber);
                    }

                    entries.add(entry);

                } catch (NumberFormatException
                        | java.time.DateTimeException ex) {
                    throw new IOException(
                        "Invalid value at CSV line " + lineNumber, ex);
                }
            }
        }

        return entries;
    }

    private Item findItem(List<Item> items, int id) {
        for (Item item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    private Shop findShop(List<Shop> shops, int id) {
        for (Shop shop : shops) {
            if (shop.getId() == id) {
                return shop;
            }
        }
        return null;
    }

    private User findUser(List<User> users, int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }
}

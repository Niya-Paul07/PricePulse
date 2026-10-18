package com.pricepulse.storage;

import com.pricepulse.Item;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ItemStorage {

    private final Path file = Paths.get("data", "items.csv");

    public void saveItems(List<Item> items) throws IOException {
        Files.createDirectories(file.getParent());

        List<String> lines = new ArrayList<>();
        lines.add("id,name,category,unit");

        for (Item item : items) {
            lines.add(item.getId() + ","
                    + clean(item.getName()) + ","
                    + clean(item.getCategory()) + ","
                    + clean(item.getUnit()));
        }

        Files.write(file, lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    public List<Item> loadItems() throws IOException {
        List<Item> items = new ArrayList<>();

        if (!Files.exists(file)) {
            return items;
        }

        List<String> lines = Files.readAllLines(file);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",", -1);

            if (parts.length != 4) {
                throw new IOException(
                        "Invalid item data on line " + (i + 1));
            }

            try {
                items.add(new Item(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        parts[2],
                        parts[3]));
            } catch (NumberFormatException ex) {
                throw new IOException(
                        "Invalid item ID on line " + (i + 1), ex);
            }
        }

        return items;
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\n")
                || value.contains("\r")) {
            throw new IllegalArgumentException(
                    "Commas and line breaks are not supported in item fields.");
        }

        return value;
    }
}
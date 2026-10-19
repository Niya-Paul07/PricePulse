package com.pricepulse.storage;

import com.pricepulse.Shop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ShopStorage {

    private final Path file = Paths.get("data", "shops.csv");

    public void saveShops(List<Shop> shops) throws IOException {
        Files.createDirectories(file.getParent());

        List<String> lines = new ArrayList<>();
        lines.add("id,name,location");

        for (Shop shop : shops) {
            lines.add(shop.getId() + ","
                    + clean(shop.getName()) + ","
                    + clean(shop.getLocation()));
        }

        Files.write(file, lines,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
    }

    public List<Shop> loadShops() throws IOException {
        List<Shop> shops = new ArrayList<>();

        if (!Files.exists(file)) {
            return shops;
        }

        List<String> lines = Files.readAllLines(file);

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",", -1);

            if (parts.length != 3) {
                throw new IOException(
                        "Invalid shop data on line " + (i + 1));
            }

            try {
                shops.add(new Shop(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        parts[2]));
            } catch (NumberFormatException ex) {
                throw new IOException(
                        "Invalid shop ID on line " + (i + 1), ex);
            }
        }

        return shops;
    }

    private String clean(String value) {
        if (value == null) {
            return "";
        }

        if (value.contains(",") || value.contains("\n")
                || value.contains("\r")) {
            throw new IllegalArgumentException(
                    "Commas and line breaks are not supported in shop fields.");
        }

        return value;
    }
}

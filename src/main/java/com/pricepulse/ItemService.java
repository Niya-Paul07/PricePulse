package com.pricepulse;

import java.util.ArrayList;
import java.util.List;

public class ItemService {

    private final List<Item> items = new ArrayList<>();
    private int nextId = 1;

    public boolean addItem(Item item) {
        if (!validateItem(item)) {
            return false;
        }

        if (isDuplicate(item, -1)) {
            return false;
        }

        item.setId(nextId++);
        items.add(item);
        return true;
    }

    public Item getItem(int id) {
        for (Item item : items) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public boolean updateItem(Item updatedItem) {
        if (!validateItem(updatedItem)) {
            return false;
        }

        Item existingItem = getItem(updatedItem.getId());

        if (existingItem == null) {
            return false;
        }

        if (isDuplicate(updatedItem, updatedItem.getId())) {
            return false;
        }

        existingItem.setName(updatedItem.getName().trim());
        existingItem.setCategory(updatedItem.getCategory().trim());
        existingItem.setUnit(updatedItem.getUnit().trim());
        existingItem.setPrice(updatedItem.getPrice());
        existingItem.setShopName(updatedItem.getShopName().trim());

        return true;
    }

    public boolean deleteItem(int id) {
        return items.removeIf(item -> item.getId() == id);
    }

    private boolean isDuplicate(Item candidate, int excludedId) {
        for (Item existing : items) {
            if (existing.getId() == excludedId) {
                continue;
            }

            boolean sameName = existing.getName() != null
                    && candidate.getName() != null
                    && existing.getName().trim()
                    .equalsIgnoreCase(candidate.getName().trim());

            boolean sameShop = existing.getShopName() != null
                    && candidate.getShopName() != null
                    && existing.getShopName().trim()
                    .equalsIgnoreCase(candidate.getShopName().trim());

            if (sameName && sameShop) {
                return true;
            }
        }

        return false;
    }

    private boolean validateItem(Item item) {
        if (item == null) {
            return false;
        }

        if (item.getName() == null || item.getName().trim().isEmpty()) {
            return false;
        }

        if (item.getCategory() == null
                || item.getCategory().trim().isEmpty()) {
            return false;
        }

        if (item.getUnit() == null || item.getUnit().trim().isEmpty()) {
            return false;
        }

        if (item.getShopName() == null
                || item.getShopName().trim().isEmpty()) {
            return false;
        }

        if (!Double.isFinite(item.getPrice()) || item.getPrice() < 0) {
            return false;
        }

        return true;
    }
}
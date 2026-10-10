package com.pricepulse;

import com.pricepulse.storage.ItemStorage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemService {

    private final List<Item> items = new ArrayList<>();
    private final ItemStorage storage = new ItemStorage();

    private int nextId = 1;

    private static final String[] VALID_CATEGORIES = {
            "Grocery", "Fruits", "Vegetables", "Dairy",
            "Beverages", "Snacks", "Personal Care", "Household"
    };

    public ItemService() {
        try {
            items.addAll(storage.loadItems());

            for (Item item : items) {
                if (item.getId() >= nextId) {
                    nextId = item.getId() + 1;
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not load saved products.", ex);
        }
    }

    public boolean addItem(Item item) {
        if (!validateItem(item)) return false;

        if (isDuplicateName(item.getName(), -1)) {
            System.out.println("Item with this name already exists.");
            return false;
        }

        item.setId(nextId);
        items.add(item);

        try {
            saveItems();
            nextId++;
            System.out.println("Item added successfully.");
            return true;
        } catch (IllegalStateException ex) {
            items.remove(item);
            throw ex;
        }
    }

    public Item getItem(int id) {
        for (Item item : items) {
            if (item.getId() == id) return item;
        }
        return null;
    }

    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public boolean updateItem(Item updatedItem) {
        if (!validateItem(updatedItem)) return false;

        Item existingItem = getItem(updatedItem.getId());

        if (existingItem == null) {
            System.out.println("Item not found.");
            return false;
        }

        if (isDuplicateName(updatedItem.getName(), updatedItem.getId())) {
            System.out.println("Another item with this name already exists.");
            return false;
        }

        String oldName = existingItem.getName();
        String oldCategory = existingItem.getCategory();
        String oldUnit = existingItem.getUnit();

        existingItem.setName(updatedItem.getName());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setUnit(updatedItem.getUnit());

        try {
            saveItems();
            System.out.println("Item updated successfully.");
            return true;
        } catch (IllegalStateException ex) {
            existingItem.setName(oldName);
            existingItem.setCategory(oldCategory);
            existingItem.setUnit(oldUnit);
            throw ex;
        }
    }

    public boolean deleteItem(int id) {
        Item item = getItem(id);

        if (item == null) {
            System.out.println("Item not found.");
            return false;
        }

        items.remove(item);

        try {
            saveItems();
            System.out.println("Item deleted successfully.");
            return true;
        } catch (IllegalStateException ex) {
            items.add(item);
            throw ex;
        }
    }

    private void saveItems() {
        try {
            storage.saveItems(items);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not save products.", ex);
        }
    }

    private boolean isDuplicateName(String name, int currentId) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(name.trim())
                    && item.getId() != currentId) {
                return true;
            }
        }
        return false;
    }

    private boolean validateItem(Item item) {
        if (item == null) {
            System.out.println("Item cannot be null.");
            return false;
        }
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            System.out.println("Item name is required.");
            return false;
        }
        if (item.getCategory() == null || item.getCategory().trim().isEmpty()) {
            System.out.println("Category is required.");
            return false;
        }
        if (item.getUnit() == null || item.getUnit().trim().isEmpty()) {
            System.out.println("Unit is required.");
            return false;
        }
        if (!isValidCategory(item.getCategory())) {
            System.out.println("Invalid category.");
            return false;
        }
        return true;
    }

    private boolean isValidCategory(String category) {
        for (String validCategory : VALID_CATEGORIES) {
            if (validCategory.equalsIgnoreCase(category.trim())) {
                return true;
            }
        }
        return false;
    }
}
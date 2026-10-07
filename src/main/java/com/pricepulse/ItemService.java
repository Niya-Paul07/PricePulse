package com.pricepulse;

import java.util.ArrayList;
import java.util.List;

public class ItemService {

    private final List<Item> items = new ArrayList<>();

    private int nextId = 1;

    // Valid categories
    private static final String[] VALID_CATEGORIES = {
            "Grocery",
            "Fruits",
            "Vegetables",
            "Dairy",
            "Beverages",
            "Snacks",
            "Personal Care",
            "Household"
    };

    // CREATE
    public boolean addItem(Item item) {

        if (!validateItem(item)) {
            return false;
        }

        if (isDuplicateName(item.getName(), -1)) {
            System.out.println("Item with this name already exists.");
            return false;
        }

        item.setId(nextId++);
        items.add(item);

        System.out.println("Item added successfully.");
        return true;
    }

    // READ - one item
    public Item getItem(int id) {

        for (Item item : items) {

            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    // READ - all items
    public List<Item> getAllItems() {

        return new ArrayList<>(items);
    }

    // UPDATE
    public boolean updateItem(Item updatedItem) {

        if (!validateItem(updatedItem)) {
            return false;
        }

        Item existingItem = getItem(updatedItem.getId());

        if (existingItem == null) {
            System.out.println("Item not found.");
            return false;
        }

        // Ignore the same item's name while checking duplicates
        if (isDuplicateName(updatedItem.getName(), updatedItem.getId())) {
            System.out.println("Another item with this name already exists.");
            return false;
        }

        existingItem.setName(updatedItem.getName());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setUnit(updatedItem.getUnit());

        System.out.println("Item updated successfully.");
        return true;
    }

    // DELETE
    public boolean deleteItem(int id) {

        Item item = getItem(id);

        if (item == null) {
            System.out.println("Item not found.");
            return false;
        }

        items.remove(item);

        System.out.println("Item deleted successfully.");
        return true;
    }

    // Check duplicate name
    private boolean isDuplicateName(String name, int currentId) {

        for (Item item : items) {

            if (item.getName().equalsIgnoreCase(name.trim())
                    && item.getId() != currentId) {

                return true;
            }
        }

        return false;
    }

    // Validation
    private boolean validateItem(Item item) {

        if (item == null) {
            System.out.println("Item cannot be null.");
            return false;
        }

        if (item.getName() == null ||
                item.getName().trim().isEmpty()) {

            System.out.println("Item name is required.");
            return false;
        }

        if (item.getCategory() == null ||
                item.getCategory().trim().isEmpty()) {

            System.out.println("Category is required.");
            return false;
        }

        if (item.getUnit() == null ||
                item.getUnit().trim().isEmpty()) {

            System.out.println("Unit is required.");
            return false;
        }

        if (!isValidCategory(item.getCategory())) {

            System.out.println("Invalid category.");
            return false;
        }

        return true;
    }

    // Check valid category
    private boolean isValidCategory(String category) {

        for (String validCategory : VALID_CATEGORIES) {

            if (validCategory.equalsIgnoreCase(category.trim())) {
                return true;
            }
        }

        return false;
    }
}
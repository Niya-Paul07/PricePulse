package com.pricepulse;

import com.pricepulse.storage.ShopStorage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShopService {

    private final List<Shop> shops = new ArrayList<>();
    private final ShopStorage storage = new ShopStorage();

    private int nextId = 1;

    public ShopService() {
        try {
            shops.addAll(storage.loadShops());

            for (Shop shop : shops) {
                if (shop.getId() >= nextId) {
                    nextId = shop.getId() + 1;
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not load saved shops.", ex);
        }
    }

    // CREATE
    public boolean addShop(Shop shop) {
        if (!validateShop(shop)) return false;

        if (isDuplicateName(shop.getName(), -1)) {
            System.out.println("Shop with this name already exists.");
            return false;
        }

        shop.setId(nextId);
        shops.add(shop);

        try {
            saveShops();
            nextId++;
            System.out.println("Shop added successfully.");
            return true;
        } catch (IllegalStateException ex) {
            shops.remove(shop);
            throw ex;
        }
    }

    // READ - one shop
    public Shop getShop(int id) {
        for (Shop shop : shops) {
            if (shop.getId() == id) return shop;
        }
        return null;
    }

    // READ - all shops
    public List<Shop> getAllShops() {
        return new ArrayList<>(shops);
    }

    // UPDATE
    public boolean updateShop(Shop updatedShop) {
        if (!validateShop(updatedShop)) return false;

        Shop existingShop = getShop(updatedShop.getId());

        if (existingShop == null) {
            System.out.println("Shop not found.");
            return false;
        }

        if (isDuplicateName(updatedShop.getName(), updatedShop.getId())) {
            System.out.println("Another shop with this name already exists.");
            return false;
        }

        String oldName = existingShop.getName();
        String oldLocation = existingShop.getLocation();

        existingShop.setName(updatedShop.getName());
        existingShop.setLocation(updatedShop.getLocation());

        try {
            saveShops();
            System.out.println("Shop updated successfully.");
            return true;
        } catch (IllegalStateException ex) {
            existingShop.setName(oldName);
            existingShop.setLocation(oldLocation);
            throw ex;
        }
    }

    // DELETE
    public boolean deleteShop(int id) {
        Shop shop = getShop(id);

        if (shop == null) {
            System.out.println("Shop not found.");
            return false;
        }

        shops.remove(shop);

        try {
            saveShops();
            System.out.println("Shop deleted successfully.");
            return true;
        } catch (IllegalStateException ex) {
            shops.add(shop);
            throw ex;
        }
    }

    private void saveShops() {
        try {
            storage.saveShops(shops);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not save shops.", ex);
        }
    }

    private boolean isDuplicateName(String name, int currentId) {
        for (Shop shop : shops) {
            if (shop.getName().equalsIgnoreCase(name.trim())
                    && shop.getId() != currentId) {
                return true;
            }
        }
        return false;
    }

    private boolean validateShop(Shop shop) {
        if (shop == null) {
            System.out.println("Shop cannot be null.");
            return false;
        }

        if (shop.getName() == null || shop.getName().trim().isEmpty()) {
            System.out.println("Shop name is required.");
            return false;
        }

        if (shop.getLocation() == null
                || shop.getLocation().trim().isEmpty()) {
            System.out.println("Shop location is required.");
            return false;
        }

        return true;
    }
}
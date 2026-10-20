
package com.pricepulse;

import com.pricepulse.model.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ShopService {

    // CREATE
    public boolean addShop(Shop shop) {

        if (!validateShop(shop)) {
            return false;
        }

        if (isDuplicateName(shop.getName(), -1)) {
            System.out.println("Shop with this name already exists.");
            return false;
        }

        String sql = "INSERT INTO shops (shop_name, location) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, shop.getName().trim());
            ps.setString(2, shop.getLocation().trim());

            int rows = ps.executeUpdate();

            if (rows == 1) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        shop.setId(keys.getInt(1));
                    }
                }

                System.out.println("Shop added successfully.");
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error adding shop: " + e.getMessage());
        }

        return false;
    }

    // READ - one shop
    public Shop getShop(int id) {

        String sql =
                "SELECT shop_id, shop_name, location "
                + "FROM shops WHERE shop_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Shop(
                            rs.getInt("shop_id"),
                            rs.getString("shop_name"),
                            rs.getString("location")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding shop: " + e.getMessage());
        }

        return null;
    }

    // READ - all shops
    public List<Shop> getAllShops() {

        List<Shop> shops = new ArrayList<>();

        String sql =
                "SELECT shop_id, shop_name, location "
                + "FROM shops ORDER BY shop_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                shops.add(new Shop(
                        rs.getInt("shop_id"),
                        rs.getString("shop_name"),
                        rs.getString("location")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error loading shops: " + e.getMessage());
        }

        return shops;
    }

    // UPDATE
    public boolean updateShop(Shop shop) {

        if (!validateShop(shop)) {
            return false;
        }

        if (getShop(shop.getId()) == null) {
            System.out.println("Shop not found.");
            return false;
        }

        if (isDuplicateName(shop.getName(), shop.getId())) {
            System.out.println(
                    "Another shop with this name already exists.");
            return false;
        }

        String sql =
                "UPDATE shops SET shop_name = ?, location = ? "
                + "WHERE shop_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, shop.getName().trim());
            ps.setString(2, shop.getLocation().trim());
            ps.setInt(3, shop.getId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Error updating shop: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteShop(int id) {

        String sql = "DELETE FROM shops WHERE shop_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            boolean deleted = ps.executeUpdate() == 1;

            if (!deleted) {
                System.out.println("Shop not found.");
            }

            return deleted;

        } catch (SQLException e) {
            System.out.println(
                    "Could not delete shop. It may have existing price "
                    + "entries. " + e.getMessage());
            return false;
        }
    }

    // Check duplicate shop name
    private boolean isDuplicateName(String name, int excludedId) {

        String sql =
                "SELECT shop_id FROM shops "
                + "WHERE LOWER(shop_name) = LOWER(?) "
                + "AND shop_id <> ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setInt(2, excludedId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error checking duplicate shop: " + e.getMessage());
            return true;
        }
    }

    // Validation
    private boolean validateShop(Shop shop) {

        if (shop == null) {
            System.out.println("Shop cannot be null.");
            return false;
        }

        if (shop.getName() == null
                || shop.getName().trim().isEmpty()) {
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


package com.pricepulse;

import com.pricepulse.model.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ItemService {

    // CREATE: save product and its shop-specific price
    public boolean addItem(Item item) {
        if (!validateItem(item)) {
            System.out.println("Invalid product details.");
            return false;
        }

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int shopId = findShopId(con, item.getShopName());

                if (shopId == -1) {
                    con.rollback();
                    System.out.println("Shop not found.");
                    return false;
                }

                if (isDuplicate(con, item.getName(), shopId, -1)) {
                    con.rollback();
                    System.out.println(
                            "This product already exists at this shop.");
                    return false;
                }

                String productSql =
                        "INSERT INTO products "
                        + "(product_name, category, unit) "
                        + "VALUES (?, ?, ?)";

                int productId;

                try (PreparedStatement ps = con.prepareStatement(
                        productSql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setString(1, item.getName().trim());
                    ps.setString(2, item.getCategory().trim());
                    ps.setString(3, item.getUnit().trim());

                    ps.executeUpdate();

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) {
                            throw new SQLException(
                                    "Could not obtain product ID.");
                        }
                        productId = keys.getInt(1);
                    }
                }

                String priceSql =
                        "INSERT INTO price_entries "
                        + "(product_id, shop_id, price, entry_date) "
                        + "VALUES (?, ?, ?, CURRENT_DATE)";

                try (PreparedStatement ps =
                        con.prepareStatement(priceSql)) {

                    ps.setInt(1, productId);
                    ps.setInt(2, shopId);
                    ps.setDouble(3, item.getPrice());
                    ps.executeUpdate();
                }

                con.commit();
                item.setId(productId);
                System.out.println("Product saved successfully.");
                return true;

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error saving product: "
                        + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database connection error: "
                    + e.getMessage());
            return false;
        }
    }

    // READ: get one product with its shop and price
    public Item getItem(int id) {
        String sql =
                "SELECT p.product_id, p.product_name, p.category, p.unit, "
                + "pe.price, s.shop_name "
                + "FROM products p "
                + "JOIN price_entries pe ON p.product_id = pe.product_id "
                + "JOIN shops s ON pe.shop_id = s.shop_id "
                + "WHERE p.product_id = ? "
                + "ORDER BY pe.entry_date DESC LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapItem(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding product: "
                    + e.getMessage());
        }

        return null;
    }

    // READ: get products and their shop-specific prices
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        String sql =
                "SELECT p.product_id, p.product_name, p.category, p.unit, "
                + "pe.price, s.shop_name "
                + "FROM products p "
                + "JOIN price_entries pe ON p.product_id = pe.product_id "
                + "JOIN shops s ON pe.shop_id = s.shop_id "
                + "ORDER BY p.product_name, s.shop_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapItem(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error loading products: "
                    + e.getMessage());
        }

        return items;
    }

    // UPDATE: update product details and its price at the selected shop
    public boolean updateItem(Item item) {
        if (!validateItem(item)) {
            return false;
        }

        String findSql =
                "SELECT pe.shop_id FROM price_entries pe "
                + "WHERE pe.product_id = ? "
                + "AND pe.shop_id = (SELECT s.shop_id FROM shops s "
                + "WHERE LOWER(s.shop_name) = LOWER(?) LIMIT 1)";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int shopId = findShopId(con, item.getShopName());

                if (shopId == -1) {
                    con.rollback();
                    System.out.println("Shop not found.");
                    return false;
                }

                boolean priceEntryExists = false;

                try (PreparedStatement ps =
                        con.prepareStatement(findSql)) {
                    ps.setInt(1, item.getId());
                    ps.setString(2, item.getShopName().trim());

                    try (ResultSet rs = ps.executeQuery()) {
                        priceEntryExists = rs.next();
                    }
                }

                if (!priceEntryExists) {
                    con.rollback();
                    System.out.println(
                            "Product price entry not found for this shop.");
                    return false;
                }

                if (isDuplicate(con, item.getName(), shopId,
                        item.getId())) {
                    con.rollback();
                    System.out.println(
                            "Another product with this name exists here.");
                    return false;
                }

                String productSql =
                        "UPDATE products SET product_name = ?, "
                        + "category = ?, unit = ? WHERE product_id = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(productSql)) {
                    ps.setString(1, item.getName().trim());
                    ps.setString(2, item.getCategory().trim());
                    ps.setString(3, item.getUnit().trim());
                    ps.setInt(4, item.getId());

                    if (ps.executeUpdate() != 1) {
                        con.rollback();
                        return false;
                    }
                }

                String priceSql =
                        "UPDATE price_entries SET price = ?, "
                        + "entry_date = CURRENT_DATE "
                        + "WHERE product_id = ? AND shop_id = ?";

                try (PreparedStatement ps =
                        con.prepareStatement(priceSql)) {
                    ps.setDouble(1, item.getPrice());
                    ps.setInt(2, item.getId());
                    ps.setInt(3, shopId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error updating product: "
                        + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // DELETE: remove price entries before deleting the product
    public boolean deleteItem(int id) {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM price_entries WHERE product_id = ?")) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }

                int rows;

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM products WHERE product_id = ?")) {
                    ps.setInt(1, id);
                    rows = ps.executeUpdate();
                }

                con.commit();
                return rows == 1;

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Error deleting product: "
                        + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    private int findShopId(Connection con, String shopName)
            throws SQLException {

        String sql =
                "SELECT shop_id FROM shops "
                + "WHERE LOWER(shop_name) = LOWER(?) LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, shopName.trim());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("shop_id") : -1;
            }
        }
    }

    private boolean isDuplicate(Connection con, String name,
                                int shopId, int excludedProductId)
            throws SQLException {

        String sql =
                "SELECT p.product_id FROM products p "
                + "JOIN price_entries pe ON p.product_id = pe.product_id "
                + "WHERE LOWER(p.product_name) = LOWER(?) "
                + "AND pe.shop_id = ? AND p.product_id <> ? LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name.trim());
            ps.setInt(2, shopId);
            ps.setInt(3, excludedProductId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Item mapItem(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("category"),
                rs.getString("unit"),
                rs.getDouble("price"),
                rs.getString("shop_name")
        );
    }

    private boolean validateItem(Item item) {
        return item != null
                && item.getName() != null
                && !item.getName().trim().isEmpty()
                && item.getCategory() != null
                && !item.getCategory().trim().isEmpty()
                && item.getUnit() != null
                && !item.getUnit().trim().isEmpty()
                && item.getShopName() != null
                && !item.getShopName().trim().isEmpty()
                && Double.isFinite(item.getPrice())
                && item.getPrice() >= 0;
    }
}

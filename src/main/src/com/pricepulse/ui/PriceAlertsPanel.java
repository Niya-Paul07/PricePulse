
package com.pricepulse.ui;

import com.pricepulse.model.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PriceAlertsPanel extends JPanel {

    // Theme
    private final Color GREEN = new Color(32, 112, 81);
    private final Color DARK_GREEN = new Color(25, 67, 52);
    private final Color BACKGROUND = new Color(247, 250, 246);
    private final Color MINT = new Color(222, 241, 226);
    private final Color TEXT = new Color(38, 59, 48);
    private final Color MUTED = new Color(108, 128, 115);
    private final Color WHITE = Color.WHITE;
    private final Color BORDER = new Color(225, 234, 225);

    private JComboBox<ProductOption> productCombo;
    private JTextField targetPriceField;
    private JTable alertsTable;
    private DefaultTableModel tableModel;

    private JButton createButton;
    private JButton deleteButton;
    private JButton refreshButton;

    public PriceAlertsPanel() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);

        // All page content goes inside one scrollable panel.
        JPanel page = new JPanel(new BorderLayout(0, 22));
        page.setBackground(BACKGROUND);
        page.setBorder(new EmptyBorder(28, 30, 28, 30));

        page.add(createHeader(), BorderLayout.NORTH);
        page.add(createMainContent(), BorderLayout.CENTER);

        JScrollPane pageScroll = new JScrollPane(page);
        pageScroll.setBorder(null);
        pageScroll.setBackground(BACKGROUND);
        pageScroll.getViewport().setBackground(BACKGROUND);
        pageScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pageScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        add(pageScroll, BorderLayout.CENTER);

        loadProducts();
        loadAlerts();
    }

    // ---------------- HEADER ----------------

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 10));
        header.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(
                new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Price Alerts");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(DARK_GREEN);

        JLabel subtitle = new JLabel(
                "Track prices and know when a product reaches your target.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(subtitle);

        refreshButton = new JButton("Refresh");
        styleButton(refreshButton, false);
        refreshButton.addActionListener(e -> refreshData());

        header.add(titlePanel, BorderLayout.CENTER);
        header.add(refreshButton, BorderLayout.EAST);

        return header;
    }

    // ---------------- MAIN CONTENT ----------------

    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);

        content.add(createAlertForm(), BorderLayout.NORTH);
        content.add(createAlertsSection(), BorderLayout.CENTER);

        return content;
    }

    // ---------------- CREATE ALERT FORM ----------------

    private JPanel createAlertForm() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 16));

        JLabel heading = new JLabel("Create a new alert");
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));
        heading.setForeground(DARK_GREEN);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        fields.add(createFieldLabel("Product"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        fields.add(createFieldLabel("Target price (₹)"), gbc);

        productCombo = new JComboBox<>();
        productCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        productCombo.setBackground(WHITE);
        productCombo.setForeground(TEXT);
        productCombo.setPreferredSize(new Dimension(220, 39));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        fields.add(productCombo, gbc);

        targetPriceField = new JTextField();
        targetPriceField.setFont(
                new Font("SansSerif", Font.PLAIN, 13));
        targetPriceField.setForeground(TEXT);
        targetPriceField.setPreferredSize(new Dimension(180, 39));
        targetPriceField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)));

        gbc.gridx = 1;
        gbc.weightx = 0.5;
        fields.add(targetPriceField, gbc);

        createButton = new JButton("+ Create Alert");
        styleButton(createButton, true);
        createButton.addActionListener(e -> createAlert());

        JPanel buttonRow = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(createButton);

        JPanel formBody = new JPanel(new BorderLayout(0, 12));
        formBody.setOpaque(false);
        formBody.add(fields, BorderLayout.CENTER);
        formBody.add(buttonRow, BorderLayout.SOUTH);

        card.add(heading, BorderLayout.NORTH);
        card.add(formBody, BorderLayout.CENTER);

        return card;
    }

    // ---------------- ALERTS TABLE ----------------

    private JPanel createAlertsSection() {
        JPanel section = new JPanel(new BorderLayout(0, 13));
        section.setOpaque(false);

        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setOpaque(false);

        JLabel heading = new JLabel("Your alerts");
        heading.setFont(new Font("SansSerif", Font.BOLD, 19));
        heading.setForeground(DARK_GREEN);

        JLabel hint = new JLabel(
                "Prices are based on the latest entry at each shop.");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hint.setForeground(MUTED);

        JPanel headingGroup = new JPanel();
        headingGroup.setOpaque(false);
        headingGroup.setLayout(
                new BoxLayout(headingGroup, BoxLayout.Y_AXIS));
        headingGroup.add(heading);
        headingGroup.add(Box.createVerticalStrut(4));
        headingGroup.add(hint);

        sectionHeader.add(headingGroup, BorderLayout.WEST);

        String[] columns = {
                "ID", "Product", "Target Price", "Lowest Price",
                "Shop", "Status", "Created"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        alertsTable = new JTable(tableModel);
        alertsTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        alertsTable.setForeground(TEXT);
        alertsTable.setBackground(WHITE);
        alertsTable.setRowHeight(38);
        alertsTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        alertsTable.setShowGrid(false);
        alertsTable.setIntercellSpacing(new Dimension(0, 0));
        alertsTable.setFillsViewportHeight(true);
        alertsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        alertsTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        alertsTable.getTableHeader().setForeground(DARK_GREEN);
        alertsTable.getTableHeader().setBackground(MINT);
        alertsTable.getTableHeader().setPreferredSize(
                new Dimension(0, 38));
        alertsTable.getTableHeader().setReorderingAllowed(false);

        int[] widths = {45, 155, 105, 105, 145, 155, 150};

        for (int i = 0; i < widths.length; i++) {
            alertsTable.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        // The table has its own scrollbars when needed.
        JScrollPane tableScroll = new JScrollPane(alertsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        tableScroll.getViewport().setBackground(WHITE);
        tableScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScroll.setPreferredSize(new Dimension(850, 250));

        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(tableScroll, BorderLayout.CENTER);

        deleteButton = new JButton("Delete Selected Alert");
        styleButton(deleteButton, false);
        deleteButton.addActionListener(e -> deleteSelectedAlert());

        JPanel bottom = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false);
        bottom.add(deleteButton);

        section.add(sectionHeader, BorderLayout.NORTH);
        section.add(tableCard, BorderLayout.CENTER);
        section.add(bottom, BorderLayout.SOUTH);

        return section;
    }

    // ---------------- CREATE ALERT ----------------

    private void createAlert() {
        ProductOption selected =
                (ProductOption) productCombo.getSelectedItem();

        if (selected == null) {
            showMessage(
                    "No products are available. Add a product first.",
                    "No Products",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String priceText = targetPriceField.getText().trim();

        if (priceText.isEmpty()) {
            showMessage(
                    "Please enter a target price.",
                    "Missing Price",
                    JOptionPane.WARNING_MESSAGE);
            targetPriceField.requestFocusInWindow();
            return;
        }

        BigDecimal targetPrice;

        try {
            targetPrice = new BigDecimal(priceText);

            if (targetPrice.compareTo(BigDecimal.ZERO) <= 0
                    || targetPrice.scale() > 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showMessage(
                    "Enter a valid price greater than zero "
                            + "with no more than two decimal places.",
                    "Invalid Price",
                    JOptionPane.WARNING_MESSAGE);
            targetPriceField.requestFocusInWindow();
            return;
        }

        String sql =
                "INSERT INTO price_alerts (product_id, target_price) "
                        + "VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, selected.id);
            ps.setBigDecimal(2, targetPrice);
            ps.executeUpdate();

            targetPriceField.setText("");
            loadAlerts();

            showMessage(
                    "Your price alert has been saved!",
                    "Alert Created",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            showDatabaseError("creating the alert", ex);
        }
    }

    // ---------------- LOAD PRODUCTS ----------------

    private void loadProducts() {
        if (productCombo == null) {
            return;
        }

        productCombo.removeAllItems();

        String sql =
                "SELECT product_id, product_name "
                        + "FROM products ORDER BY product_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productCombo.addItem(new ProductOption(
                        rs.getInt("product_id"),
                        rs.getString("product_name")));
            }

        } catch (SQLException ex) {
            showDatabaseError("loading products", ex);
        }

        boolean hasProducts = productCombo.getItemCount() > 0;
        productCombo.setEnabled(hasProducts);
        createButton.setEnabled(hasProducts);
    }

    // ---------------- LOAD ALERTS ----------------

    private void loadAlerts() {
        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        /*
         * First find the latest entry for every product at every shop.
         * Then select the lowest of those latest prices for each alert.
         */
        String sql =
                "SELECT a.alert_id, p.product_name, a.target_price, "
                        + "a.created_at, latest.price AS lowest_price, "
                        + "latest.shop_name AS cheapest_shop "
                        + "FROM price_alerts a "
                        + "JOIN products p ON p.product_id = a.product_id "
                        + "LEFT JOIN ( "
                        + " SELECT current_prices.product_id, "
                        + "        current_prices.price, "
                        + "        current_prices.shop_name "
                        + " FROM ( "
                        + "   SELECT pe.product_id, pe.shop_id, pe.price, "
                        + "          pe.entry_date, pe.price_entry_id, "
                        + "          s.shop_name, "
                        + "          ROW_NUMBER() OVER ( "
                        + "             PARTITION BY pe.product_id, pe.shop_id "
                        + "             ORDER BY pe.entry_date DESC, "
                        + "                      pe.price_entry_id DESC "
                        + "          ) AS rn "
                        + "   FROM price_entries pe "
                        + "   JOIN shops s ON s.shop_id = pe.shop_id "
                        + " ) current_prices "
                        + " WHERE current_prices.rn = 1 "
                        + ") latest ON latest.product_id = a.product_id "
                        + "AND latest.price = ( "
                        + " SELECT MIN(cp.price) "
                        + " FROM ( "
                        + "   SELECT pe2.product_id, pe2.shop_id, pe2.price, "
                        + "          ROW_NUMBER() OVER ( "
                        + "             PARTITION BY pe2.product_id, pe2.shop_id "
                        + "             ORDER BY pe2.entry_date DESC, "
                        + "                      pe2.price_entry_id DESC "
                        + "          ) AS rn "
                        + "   FROM price_entries pe2 "
                        + " ) cp "
                        + " WHERE cp.product_id = a.product_id "
                        + "   AND cp.rn = 1 "
                        + ") "
                        + "ORDER BY a.created_at DESC, a.alert_id DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int alertId = rs.getInt("alert_id");
                String productName = rs.getString("product_name");
                BigDecimal target = rs.getBigDecimal("target_price");
                BigDecimal lowest = rs.getBigDecimal("lowest_price");
                String shop = rs.getString("cheapest_shop");
                String created = rs.getString("created_at");

                String lowestText;
                String shopText;
                String status;

                if (lowest == null) {
                    lowestText = "—";
                    shopText = "—";
                    status = "No price recorded";
                } else {
                    lowestText = "₹" + lowest.toPlainString();
                    shopText = shop == null ? "—" : shop;

                    if (lowest.compareTo(target) <= 0) {
                        status = "Target reached";
                    } else {
                        status = "Waiting for price drop";
                    }
                }

                tableModel.addRow(new Object[]{
                        alertId,
                        productName,
                        "₹" + target.toPlainString(),
                        lowestText,
                        shopText,
                        status,
                        created
                });
            }

        } catch (SQLException ex) {
            showDatabaseError("loading alerts", ex);
        }
    }

    // ---------------- DELETE ALERT ----------------

    private void deleteSelectedAlert() {
        int selectedRow = alertsTable.getSelectedRow();

        if (selectedRow < 0) {
            showMessage(
                    "Select an alert from the table first.",
                    "No Alert Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = alertsTable.convertRowIndexToModel(selectedRow);
        int alertId = (int) tableModel.getValueAt(modelRow, 0);
        String productName =
                String.valueOf(tableModel.getValueAt(modelRow, 1));

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete the price alert for " + productName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM price_alerts WHERE alert_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, alertId);

            if (ps.executeUpdate() == 1) {
                loadAlerts();
                showMessage(
                        "The alert was deleted.",
                        "Alert Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage(
                        "This alert could not be found.",
                        "Delete Failed",
                        JOptionPane.WARNING_MESSAGE);
                loadAlerts();
            }

        } catch (SQLException ex) {
            showDatabaseError("deleting the alert", ex);
        }
    }

    // ---------------- REFRESH ----------------

    private void refreshData() {
        loadProducts();
        loadAlerts();
    }

    // ---------------- UI HELPERS ----------------

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 20, 18, 20)));
        return card;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT);
        return label;
    }

    private void styleButton(JButton button, boolean primary) {
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(11, 17, 11, 17));

        if (primary) {
            button.setBackground(GREEN);
            button.setForeground(WHITE);
        } else {
            button.setBackground(MINT);
            button.setForeground(DARK_GREEN);
        }

        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    private void showMessage(
            String message, String title, int messageType) {
        JOptionPane.showMessageDialog(
                this, message, title, messageType);
    }

    private void showDatabaseError(String action, SQLException ex) {
        ex.printStackTrace();

        showMessage(
                "There was a problem " + action + ".\n"
                        + "Please check your database connection.\n\n"
                        + "Details: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // ---------------- PRODUCT DROPDOWN ITEM ----------------

    private static class ProductOption {
        private final int id;
        private final String name;

        private ProductOption(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

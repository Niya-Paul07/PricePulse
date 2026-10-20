
package com.pricepulse.ui;

import com.pricepulse.Item;
import com.pricepulse.ItemService;
import com.pricepulse.Shop;
import com.pricepulse.ShopService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CheapestShopPanel extends JPanel {

    private final ItemService itemService;
    private final ShopService shopService;

    private JComboBox<ProductOption> productCombo;
    private JTable priceTable;
    private DefaultTableModel tableModel;

    private JLabel resultLabel;
    private JLabel savingLabel;

    private static final Color GREEN = new Color(32, 112, 81);
    private static final Color DARK_GREEN = new Color(25, 67, 52);
    private static final Color BACKGROUND = new Color(247, 250, 246);
    private static final Color MINT = new Color(222, 241, 226);
    private static final Color CREAM = new Color(250, 246, 230);
    private static final Color TEXT = new Color(38, 59, 48);
    private static final Color MUTED = new Color(108, 128, 115);
    private static final Color BORDER = new Color(225, 235, 226);
    private static final Color WHITE = Color.WHITE;

    public CheapestShopPanel(
            ItemService itemService,
            ShopService shopService) {

        this.itemService = itemService;
        this.shopService = shopService;

        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);

        refreshProducts();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Find Cheapest Shop");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(DARK_GREEN);

        JLabel subtitle = new JLabel(
                "Compare prices across shops and find the best deal.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    private JPanel buildMainContent() {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);

        // Product selection
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 6, 5, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel productLabel = new JLabel("Select Product");
        productLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        productLabel.setForeground(TEXT);

        productCombo = new JComboBox<>();
        productCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JButton compareButton = createButton("Compare Prices");
        JButton refreshButton = createButton("Refresh Products");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        searchPanel.add(productLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        searchPanel.add(productCombo, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(compareButton);
        buttons.add(refreshButton);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0;
        searchPanel.add(buttons, gbc);

        compareButton.addActionListener(e -> comparePrices());
        refreshButton.addActionListener(e -> refreshProducts());
        productCombo.addActionListener(e -> comparePrices());

        // Results
        JPanel resultsPanel = new JPanel(new BorderLayout(0, 12));
        resultsPanel.setOpaque(false);

        JPanel summary = new JPanel();
        summary.setLayout(new BoxLayout(summary, BoxLayout.Y_AXIS));
        summary.setBackground(MINT);
        summary.setBorder(new EmptyBorder(16, 18, 16, 18));

        resultLabel = new JLabel("Choose a product to compare prices.");
        resultLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        resultLabel.setForeground(DARK_GREEN);

        savingLabel = new JLabel(
                "Prices from your registered products will appear here.");
        savingLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        savingLabel.setForeground(TEXT);

        summary.add(resultLabel);
        summary.add(Box.createVerticalStrut(6));
        summary.add(savingLabel);

        String[] columns = {
            "Shop Name", "Location", "Price (₹)", "Result"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        priceTable = new JTable(tableModel);
        priceTable.setRowHeight(32);
        priceTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        priceTable.setForeground(TEXT);
        priceTable.setGridColor(BORDER);
        priceTable.setShowVerticalLines(false);
        priceTable.setSelectionBackground(MINT);

        priceTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        priceTable.getTableHeader().setBackground(CREAM);
        priceTable.getTableHeader().setForeground(DARK_GREEN);
        priceTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(priceTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);

        resultsPanel.add(summary, BorderLayout.NORTH);
        resultsPanel.add(scroll, BorderLayout.CENTER);

        content.add(searchPanel, BorderLayout.NORTH);
        content.add(resultsPanel, BorderLayout.CENTER);

        return content;
    }

    // Refresh product choices using the shared ItemService
    private void refreshProducts() {
        ProductOption previous = (ProductOption) productCombo.getSelectedItem();

        productCombo.removeAllItems();

        Map<String, ProductOption> options = new LinkedHashMap<>();

        for (Item item : itemService.getAllItems()) {
            String key = item.getName() + "\u0000" + item.getUnit();

            options.putIfAbsent(
                    key,
                    new ProductOption(item.getName(), item.getUnit()));
        }

        for (ProductOption option : options.values()) {
            productCombo.addItem(option);
        }

        if (previous != null) {
            for (int i = 0; i < productCombo.getItemCount(); i++) {
                ProductOption option = productCombo.getItemAt(i);

                if (option.matches(previous.name, previous.unit)) {
                    productCombo.setSelectedIndex(i);
                    return;
                }
            }
        }

        if (productCombo.getItemCount() > 0) {
            productCombo.setSelectedIndex(0);
        } else {
            tableModel.setRowCount(0);
            resultLabel.setText("No products available");
            savingLabel.setText(
                    "Add a product and its shop price first.");
        }
    }

    // Compare the selected product at every shop
    private void comparePrices() {
        ProductOption selected =
                (ProductOption) productCombo.getSelectedItem();

        if (selected == null) {
            tableModel.setRowCount(0);
            resultLabel.setText("No product selected");
            savingLabel.setText(
                    "Register a shop and add a product before comparing.");
            return;
        }

        List<Item> matches = new ArrayList<>();

        for (Item item : itemService.getAllItems()) {
            if (selected.matches(item.getName(), item.getUnit())) {
                matches.add(item);
            }
        }

        matches.sort(Comparator.comparingDouble(Item::getPrice));

        tableModel.setRowCount(0);

        if (matches.isEmpty()) {
            resultLabel.setText("No prices found");
            savingLabel.setText("Add prices for this product.");
            return;
        }

        double cheapest = matches.get(0).getPrice();
        double highest = matches.get(matches.size() - 1).getPrice();

        for (Item item : matches) {
            String location = "Not available";

            for (Shop shop : shopService.getAllShops()) {
                if (shop.getName() != null
                        && shop.getName().equalsIgnoreCase(
                                item.getShopName())) {
                    location = shop.getLocation();
                    break;
                }
            }

            String status = Double.compare(item.getPrice(), cheapest) == 0
                    ? "CHEAPEST"
                    : "";

            tableModel.addRow(new Object[]{
                    item.getShopName(),
                    location,
                    String.format("%.2f", item.getPrice()),
                    status
            });
        }

        resultLabel.setText(
                "Lowest price: ₹" + String.format("%.2f", cheapest)
                + " at " + matches.get(0).getShopName());

        if (matches.size() == 1) {
            savingLabel.setText(
                    "Only one shop has a recorded price for this product.");
        } else {
            savingLabel.setText(
                    "Potential saving compared with the highest listed "
                    + "price: ₹" + String.format("%.2f", highest - cheapest));
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(GREEN);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(9, 13, 9, 13));
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
        return button;
    }

    private static class ProductOption {
        private final String name;
        private final String unit;

        ProductOption(String name, String unit) {
            this.name = name;
            this.unit = unit;
        }

        boolean matches(String otherName, String otherUnit) {
            return otherName != null
                    && otherUnit != null
                    && name.equalsIgnoreCase(otherName)
                    && unit.equalsIgnoreCase(otherUnit);
        }

        @Override
        public String toString() {
            return name + " (" + unit + ")";
        }
    }
}

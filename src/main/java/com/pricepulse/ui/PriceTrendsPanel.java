
package com.pricepulse.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PriceTrendsPanel extends JPanel {

    private final Color green = new Color(36, 105, 75);
    private final Color cream = new Color(250, 248, 240);
    private final Color muted = new Color(100, 105, 95);

    private final JComboBox<String> productCombo =
            new JComboBox<>(new String[]{
                    "Rice", "Milk", "Tomatoes", "Cooking Oil", "Sugar"
            });

    private final JComboBox<String> shopCombo =
            new JComboBox<>(new String[]{
                    "Fresh Mart", "Green Basket", "City Supermarket"
            });

    private final JLabel latestValue = new JLabel("—");
    private final JLabel previousValue = new JLabel("—");
    private final JLabel trendValue = new JLabel("—");
    private final JLabel changeValue = new JLabel("—");

    private final JLabel demoLabel =
            new JLabel("DEMO DATA • Not connected to a database");

    private final DecimalFormat money =
            new DecimalFormat("#,##0.00");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Date", "Product", "Shop", "Price (₹)"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable historyTable = new JTable(tableModel);

    // Sample price record
    private static class DemoPrice {
        String date;
        String product;
        String shop;
        double price;

        DemoPrice(String date, String product,
                  String shop, double price) {
            this.date = date;
            this.product = product;
            this.shop = shop;
            this.price = price;
        }
    }

    // Temporary sample records for UI demonstration
    private final List<DemoPrice> demoPrices = new ArrayList<>();

    public PriceTrendsPanel() {
        setLayout(new BorderLayout(18, 18));
        setBackground(cream);
        setBorder(new EmptyBorder(22, 22, 22, 22));

        loadDemoData();
        buildUI();

        productCombo.addActionListener(e -> refreshTrends());
        shopCombo.addActionListener(e -> refreshTrends());

        refreshTrends();
    }

    private void loadDemoData() {
        // Rice
        demoPrices.add(new DemoPrice("01 Oct 2026",
                "Rice", "Fresh Mart", 58.00));
        demoPrices.add(new DemoPrice("05 Oct 2026",
                "Rice", "Fresh Mart", 60.00));
        demoPrices.add(new DemoPrice("10 Oct 2026",
                "Rice", "Fresh Mart", 62.00));
        demoPrices.add(new DemoPrice("15 Oct 2026",
                "Rice", "Fresh Mart", 61.00));

        demoPrices.add(new DemoPrice("01 Oct 2026",
                "Rice", "Green Basket", 56.00));
        demoPrices.add(new DemoPrice("08 Oct 2026",
                "Rice", "Green Basket", 57.00));
        demoPrices.add(new DemoPrice("15 Oct 2026",
                "Rice", "Green Basket", 59.00));

        // Milk
        demoPrices.add(new DemoPrice("01 Oct 2026",
                "Milk", "Fresh Mart", 28.00));
        demoPrices.add(new DemoPrice("08 Oct 2026",
                "Milk", "Fresh Mart", 29.00));
        demoPrices.add(new DemoPrice("15 Oct 2026",
                "Milk", "Fresh Mart", 30.00));

        demoPrices.add(new DemoPrice("01 Oct 2026",
                "Milk", "City Supermarket", 30.00));
        demoPrices.add(new DemoPrice("08 Oct 2026",
                "Milk", "City Supermarket", 29.50));
        demoPrices.add(new DemoPrice("15 Oct 2026",
                "Milk", "City Supermarket", 29.00));

        // Tomatoes
        demoPrices.add(new DemoPrice("01 Oct 2026",
                "Tomatoes", "Fresh Mart", 40.00));
        demoPrices.add(new DemoPrice("08 Oct 2026",
                "Tomatoes", "Fresh Mart", 35.00));
        demoPrices.add(new DemoPrice("15 Oct 2026",
                "Tomatoes", "Fresh Mart", 32.00));

        // Cooking Oil
        demoPrices.add(new DemoPrice("01 Oct 2026",
                "Cooking Oil", "Green Basket", 145.00));
        demoPrices.add(new DemoPrice("08 Oct 2026",
                "Cooking Oil", "Green Basket", 148.00));
        demoPrices.add(new DemoPrice("15 Oct 2026",
                "Cooking Oil", "Green Basket", 152.00));

        // Sugar
        demoPrices.add(new DemoPrice("01 Oct 2026",
                "Sugar", "City Supermarket", 44.00));
        demoPrices.add(new DemoPrice("08 Oct 2026",
                "Sugar", "City Supermarket", 43.00));
        demoPrices.add(new DemoPrice("15 Oct 2026",
                "Sugar", "City Supermarket", 42.00));
    }

    private void buildUI() {
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JLabel title = new JLabel("Price Trends");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(green);

        JLabel subtitle = new JLabel(
                "Understand how product prices change over time");
        subtitle.setForeground(muted);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 6));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        demoLabel.setForeground(new Color(155, 105, 35));
        demoLabel.setFont(new Font("SansSerif", Font.BOLD, 11));

        heading.add(titleBox, BorderLayout.WEST);
        heading.add(demoLabel, BorderLayout.EAST);

        JPanel filters = new JPanel(new GridLayout(2, 2, 10, 8));
        filters.setOpaque(false);

        filters.add(new JLabel("Product"));
        filters.add(new JLabel("Shop"));
        filters.add(productCombo);
        filters.add(shopCombo);

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);
        top.add(filters, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        JPanel summaries = new JPanel(new GridLayout(1, 4, 12, 0));
        summaries.setOpaque(false);

        summaries.add(createSummaryCard("Latest Price", latestValue));
        summaries.add(createSummaryCard("Previous Price", previousValue));
        summaries.add(createSummaryCard("Trend", trendValue));
        summaries.add(createSummaryCard("Price Change", changeValue));

        historyTable.setRowHeight(32);
        historyTable.setFillsViewportHeight(true);
        historyTable.setShowGrid(false);
        historyTable.setSelectionBackground(
                new Color(220, 238, 225));
        historyTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));

        JPanel historyCard = new JPanel(new BorderLayout(0, 12));
        historyCard.setBackground(Color.WHITE);
        historyCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 220)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel historyTitle =
                new JLabel("Recorded Price History");
        historyTitle.setFont(
                new Font("SansSerif", Font.BOLD, 17));
        historyTitle.setForeground(green);

        historyCard.add(historyTitle, BorderLayout.NORTH);
        historyCard.add(
                new JScrollPane(historyTable),
                BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(summaries, BorderLayout.NORTH);
        center.add(historyCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private JPanel createSummaryCard(String heading, JLabel value) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 230, 220)),
                BorderFactory.createEmptyBorder(16, 12, 16, 12)));

        JLabel label = new JLabel(heading);
        label.setForeground(muted);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));

        value.setForeground(green);
        value.setFont(new Font("SansSerif", Font.BOLD, 16));

        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private void refreshTrends() {
        tableModel.setRowCount(0);

        String product = (String) productCombo.getSelectedItem();
        String shop = (String) shopCombo.getSelectedItem();

        if (product == null || shop == null) {
            resetSummary();
            return;
        }

        List<DemoPrice> history = new ArrayList<>();

        for (DemoPrice entry : demoPrices) {
            if (entry.product.equals(product)
                    && entry.shop.equals(shop)) {
                history.add(entry);
            }
        }

        if (history.isEmpty()) {
            resetSummary();
            tableModel.addRow(new Object[]{
                    "No demo records", product, shop, "—"
            });
            return;
        }

        // Display oldest-to-newest history
        for (DemoPrice entry : history) {
            tableModel.addRow(new Object[]{
                    entry.date,
                    entry.product,
                    entry.shop,
                    "₹" + money.format(entry.price)
            });
        }

        DemoPrice latest = history.get(history.size() - 1);
        latestValue.setText("₹" + money.format(latest.price));

        if (history.size() >= 2) {
            DemoPrice previous = history.get(history.size() - 2);

            previousValue.setText(
                    "₹" + money.format(previous.price));

            double difference = latest.price - previous.price;
            double percentage = previous.price == 0
                    ? 0
                    : (difference / previous.price) * 100;

            if (difference > 0) {
                trendValue.setText("Rising");
            } else if (difference < 0) {
                trendValue.setText("Falling");
            } else {
                trendValue.setText("Stable");
            }

            changeValue.setText(
                    (percentage > 0 ? "+" : "")
                            + money.format(percentage) + "%");

            changeValue.setForeground(
                    difference > 0
                            ? new Color(180, 75, 55)
                            : difference < 0
                            ? green
                            : muted);
        } else {
            previousValue.setText("Not available");
            trendValue.setText("Need more data");
            changeValue.setText("—");
        }
    }

    private void resetSummary() {
        latestValue.setText("—");
        previousValue.setText("—");
        trendValue.setText("No history");
        changeValue.setText("—");
        changeValue.setForeground(green);
    }

    @Override
    public String toString() {
        return "Price Trends";
    }
}

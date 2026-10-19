
package com.pricepulse.ui;

import com.pricepulse.analysis.TrendAnalysisService;
import com.pricepulse.model.Item;
import com.pricepulse.model.Shop;
import com.pricepulse.model.PriceEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PriceTrendsPanel extends JPanel {

    private final List<PriceEntry> entries;
    private final List<Item> items;
    private final List<Shop> shops;

    private final JComboBox<Item> itemCombo;
    private final JComboBox<Shop> shopCombo;

    private final JLabel latestValue = new JLabel("—");
    private final JLabel previousValue = new JLabel("—");
    private final JLabel trendValue = new JLabel("Waiting for data");
    private final JLabel changeValue = new JLabel("—");

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{"Date", "Price (₹)"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable historyTable = new JTable(tableModel);

    private final DecimalFormat money = new DecimalFormat("#,##0.00");

    private final Color green = new Color(36, 105, 75);
    private final Color cream = new Color(250, 248, 240);

    public PriceTrendsPanel(
            List<Item> items,
            List<Shop> shops,
            List<PriceEntry> entries) {

        this.items = items;
        this.shops = shops;
        this.entries = entries;

        setLayout(new BorderLayout(18, 18));
        setBackground(cream);
        setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        itemCombo = new JComboBox<>(new DefaultComboBoxModel<>());
        shopCombo = new JComboBox<>(new DefaultComboBoxModel<>());

        buildUI();
        loadOptions();

        itemCombo.addActionListener(e -> refreshTrends());
        shopCombo.addActionListener(e -> refreshTrends());

        refreshTrends();
    }

    private void buildUI() {
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JLabel title = new JLabel("Price Trends");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(green);

        JLabel subtitle = new JLabel(
                "Understand how product prices change over time");
        subtitle.setForeground(new Color(100, 105, 95));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 6));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        heading.add(titleBox, BorderLayout.WEST);

        JPanel filters = new JPanel(new GridLayout(2, 2, 10, 8));
        filters.setOpaque(false);

        filters.add(new JLabel("Product"));
        filters.add(new JLabel("Shop"));
        filters.add(itemCombo);
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

        historyTable.setRowHeight(30);
        historyTable.setFillsViewportHeight(true);
        historyTable.setShowGrid(false);
        historyTable.setSelectionBackground(new Color(220, 238, 225));

        JPanel historyCard = new JPanel(new BorderLayout(0, 12));
        historyCard.setBackground(Color.WHITE);
        historyCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 230, 220)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));

        JLabel historyTitle = new JLabel("Recorded Price History");
        historyTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        historyTitle.setForeground(green);

        historyCard.add(historyTitle, BorderLayout.NORTH);
        historyCard.add(new JScrollPane(historyTable), BorderLayout.CENTER);

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
                BorderFactory.createLineBorder(new Color(225, 230, 220)),
                BorderFactory.createEmptyBorder(16, 12, 16, 12)));

        JLabel label = new JLabel(heading);
        label.setForeground(new Color(105, 110, 100));
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));

        value.setForeground(green);
        value.setFont(new Font("SansSerif", Font.BOLD, 17));

        card.add(label, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    private void loadOptions() {
        DefaultComboBoxModel<Item> itemModel = new DefaultComboBoxModel<>();
        for (Item item : items) {
            itemModel.addElement(item);
        }
        itemCombo.setModel(itemModel);

        DefaultComboBoxModel<Shop> shopModel = new DefaultComboBoxModel<>();
        for (Shop shop : shops) {
            shopModel.addElement(shop);
        }
        shopCombo.setModel(shopModel);
    }

    private void refreshTrends() {
        tableModel.setRowCount(0);

        Item item = (Item) itemCombo.getSelectedItem();
        Shop shop = (Shop) shopCombo.getSelectedItem();

        if (item == null || shop == null) {
            showNoData();
            return;
        }

        TrendAnalysisService service =
                new TrendAnalysisService(entries);

        List<PriceEntry> history =
                service.getPriceHistory(item, shop);

        if (history.isEmpty()) {
            showNoData();
            return;
        }

        for (PriceEntry entry : history) {
            tableModel.addRow(new Object[]{
                    entry.getDate(),
                    "₹" + money.format(entry.getPrice())
            });
        }

        PriceEntry latest = history.get(history.size() - 1);

        latestValue.setText("₹" + money.format(latest.getPrice()));

        if (history.size() >= 2) {
            PriceEntry previous = history.get(history.size() - 2);

            previousValue.setText(
                    "₹" + money.format(previous.getPrice()));

            trendValue.setText(service.getTrend(item, shop));

            double change = service.getPercentageChange(item, shop);

            changeValue.setText(
                    (change > 0 ? "+" : "") +
                    money.format(change) + "%");
        } else {
            previousValue.setText("Not available");
            trendValue.setText("Need more data");
            changeValue.setText("—");
        }
    }

    private void showNoData() {
        latestValue.setText("—");
        previousValue.setText("—");
        trendValue.setText("No history");
        changeValue.setText("—");

        if (itemCombo.getSelectedItem() != null
                && shopCombo.getSelectedItem() != null) {
            tableModel.addRow(new Object[]{
                    "No recorded prices", "—"
            });
        }
    }

    @Override
    public String toString() {
        return "Price Trends";
    }
}

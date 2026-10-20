
package com.pricepulse.ui;

import com.pricepulse.model.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PriceTrendsPanel extends JPanel {

    private static final Color CREAM = new Color(250, 248, 240);
    private static final Color CARD = Color.WHITE;
    private static final Color MINT = new Color(190, 230, 210);
    private static final Color DARK = new Color(45, 75, 65);
    private static final Color MUTED = new Color(115, 130, 122);
    private static final Color BORDER = new Color(225, 233, 226);

    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    private JComboBox<String> productCombo;
    private JComboBox<String> shopCombo;

    private JLabel latestValue;
    private JLabel previousValue;
    private JLabel trendValue;
    private JLabel changeValue;
    private JLabel statusLabel;

    private DefaultTableModel tableModel;

    private PriceLineChart lineChart;
    private ShopBarChart barChart;

    private boolean loadingFilters = false;

    public PriceTrendsPanel() {
        setLayout(new BorderLayout());
        setBackground(CREAM);

        /*
         * MAIN SCROLLING FIX:
         * All page content is placed inside contentPanel.
         */
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CREAM);
        contentPanel.setBorder(new EmptyBorder(24, 28, 28, 28));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Price Trends");
        title.setFont(new Font("SansSerif", Font.BOLD, 27));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel(
                "Track price changes and compare shops over time"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        statusLabel = new JLabel("Connecting to database...");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(MUTED);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(statusLabel, BorderLayout.EAST);

        contentPanel.add(header);
        contentPanel.add(Box.createVerticalStrut(18));

        // Filters
        JPanel filterCard = new JPanel(new GridBagLayout());
        filterCard.setBackground(CARD);
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(16, 18, 16, 18)
        ));
        filterCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        productCombo = new JComboBox<>();
        shopCombo = new JComboBox<>();

        styleCombo(productCombo);
        styleCombo(shopCombo);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        filterCard.add(createFieldLabel("Product"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        filterCard.add(productCombo, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        filterCard.add(createFieldLabel("Shop"), gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        filterCard.add(shopCombo, gbc);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
        ));
        refreshButton.setBackground(DARK);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        refreshButton.setBorder(new EmptyBorder(10, 18, 10, 18));
        refreshButton.addActionListener(e -> refreshTrends());

        gbc.gridx = 4;
        gbc.weightx = 0;
        gbc.insets = new Insets(3, 5, 3, 0);
        filterCard.add(refreshButton, gbc);

        contentPanel.add(filterCard);
        contentPanel.add(Box.createVerticalStrut(18));

        // Summary cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 14, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        latestValue = new JLabel("₹0.00");
        previousValue = new JLabel("₹0.00");
        trendValue = new JLabel("—");
        changeValue = new JLabel("0.00%");

        summaryPanel.add(createSummaryCard("Latest Price", latestValue));
        summaryPanel.add(createSummaryCard("Previous Price", previousValue));
        summaryPanel.add(createSummaryCard("Price Trend", trendValue));
        summaryPanel.add(createSummaryCard("Price Change", changeValue));

        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Charts heading
        JLabel chartsHeading = new JLabel("Visual Analysis");
        chartsHeading.setFont(new Font("SansSerif", Font.BOLD, 19));
        chartsHeading.setForeground(DARK);
        chartsHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(chartsHeading);
        contentPanel.add(Box.createVerticalStrut(12));

        // Both charts are placed vertically to fit smaller windows.
        lineChart = new PriceLineChart();
        barChart = new ShopBarChart();

        contentPanel.add(createChartCard(
                "Price History",
                "How the selected product's price changes at this shop",
                lineChart
        ));
        contentPanel.add(Box.createVerticalStrut(16));

        contentPanel.add(createChartCard(
                "Compare Shops",
                "Latest recorded price of the selected product at each shop",
                barChart
        ));
        contentPanel.add(Box.createVerticalStrut(20));

        // History heading
        JLabel historyHeading = new JLabel("Price History Records");
        historyHeading.setFont(new Font("SansSerif", Font.BOLD, 19));
        historyHeading.setForeground(DARK);
        historyHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(historyHeading);
        contentPanel.add(Box.createVerticalStrut(12));

        // History table
        String[] columns = {
                "Date", "Product", "Shop", "Price (₹)"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable historyTable = new JTable(tableModel);
        historyTable.setRowHeight(32);
        historyTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        historyTable.setForeground(DARK);
        historyTable.setBackground(Color.WHITE);
        historyTable.setGridColor(BORDER);
        historyTable.setShowVerticalLines(false);
        historyTable.setSelectionBackground(MINT);
        historyTable.setSelectionForeground(DARK);
        historyTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        historyTable.getTableHeader().setBackground(
                new Color(238, 246, 239)
        );
        historyTable.getTableHeader().setForeground(DARK);
        historyTable.setFillsViewportHeight(true);

        JScrollPane tableScroll = new JScrollPane(historyTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(BORDER));
        tableScroll.setPreferredSize(new Dimension(700, 240));
        tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(tableScroll);
        contentPanel.add(Box.createVerticalStrut(10));

        // Outer scroll pane: scrolls the entire screen.
        JScrollPane pageScroll = new JScrollPane(contentPanel);
        pageScroll.setBorder(null);
        pageScroll.setBackground(CREAM);
        pageScroll.getViewport().setBackground(CREAM);
        pageScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        pageScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        pageScroll.getVerticalScrollBar().setUnitIncrement(18);

        add(pageScroll, BorderLayout.CENTER);

        // Load database data.
        productCombo.addActionListener(e -> {
            if (!loadingFilters) {
                refreshTrends();
            }
        });

        shopCombo.addActionListener(e -> {
            if (!loadingFilters) {
                refreshTrends();
            }
        });

        loadFilters();
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(DARK);
        return label;
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(DARK);
        combo.setPreferredSize(new Dimension(150, 36));
    }

    private JPanel createSummaryCard(String title, JLabel value) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 16, 14, 12)
        ));

        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel(title);
        heading.setFont(new Font("SansSerif", Font.PLAIN, 12));
        heading.setForeground(MUTED);

        value.setFont(new Font("SansSerif", Font.BOLD, 21));
        value.setForeground(DARK);

        card.add(heading);
        card.add(Box.createVerticalStrut(12));
        card.add(value);

        return card;
    }

    private JPanel createChartCard(
            String title,
            String subtitle,
            JPanel chart
    ) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 365));
        card.setPreferredSize(new Dimension(750, 365));

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(DARK);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(MUTED);

        heading.add(titleLabel);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitleLabel);

        card.add(heading, BorderLayout.NORTH);
        card.add(chart, BorderLayout.CENTER);

        return card;
    }

    private void loadFilters() {
        loadingFilters = true;

        String oldProduct = (String) productCombo.getSelectedItem();
        String oldShop = (String) shopCombo.getSelectedItem();

        productCombo.removeAllItems();
        shopCombo.removeAllItems();

        try (Connection con = DBConnection.getConnection()) {

            String productSQL =
                    "SELECT product_name FROM products ORDER BY product_name";

            try (PreparedStatement ps = con.prepareStatement(productSQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    productCombo.addItem(rs.getString("product_name"));
                }
            }

            String shopSQL =
                    "SELECT shop_name FROM shops ORDER BY shop_name";

            try (PreparedStatement ps = con.prepareStatement(shopSQL);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    shopCombo.addItem(rs.getString("shop_name"));
                }
            }

            restoreSelection(productCombo, oldProduct);
            restoreSelection(shopCombo, oldShop);

            statusLabel.setText("Connected to MySQL");
            statusLabel.setForeground(new Color(55, 135, 95));

        } catch (SQLException ex) {
            statusLabel.setText("Database connection failed");
            statusLabel.setForeground(new Color(190, 65, 65));
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load products and shops.\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            loadingFilters = false;
        }

        refreshTrends();
    }

    private void restoreSelection(
            JComboBox<String> combo,
            String previousSelection
    ) {
        if (previousSelection == null) {
            return;
        }

        for (int i = 0; i < combo.getItemCount(); i++) {
            if (previousSelection.equals(combo.getItemAt(i))) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void refreshTrends() {
        String product = (String) productCombo.getSelectedItem();
        String shop = (String) shopCombo.getSelectedItem();

        if (product == null) {
            clearDisplay("No products available");
            return;
        }

        try {
            List<PriceRecord> history;

            if (shop == null) {
                history = new ArrayList<>();
            } else {
                history = getPriceHistory(product, shop);
            }

            updateSummary(history);
            updateHistoryTable(history);

            lineChart.setData(history);
            barChart.setData(getLatestPricesAcrossShops(product));

            if (history.isEmpty()) {
                statusLabel.setText(
                        shop == null
                                ? "No shops available"
                                : "No price history for this selection"
                );
            } else {
                statusLabel.setText("Connected to MySQL");
            }

        } catch (SQLException ex) {
            statusLabel.setText("Could not load price trends");
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Could not load price trends.\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearDisplay(String message) {
        latestValue.setText("—");
        previousValue.setText("—");
        trendValue.setText("—");
        changeValue.setText("—");

        tableModel.setRowCount(0);
        lineChart.setData(new ArrayList<>());
        barChart.setData(new LinkedHashMap<>());

        statusLabel.setText(message);
    }

    private List<PriceRecord> getPriceHistory(
            String product,
            String shop
    ) throws SQLException {

        List<PriceRecord> records = new ArrayList<>();

        String sql =
                "SELECT pe.price_entry_id, pe.entry_date, "
                        + "p.product_name, s.shop_name, pe.price "
                        + "FROM price_entries pe "
                        + "JOIN products p ON pe.product_id = p.product_id "
                        + "JOIN shops s ON pe.shop_id = s.shop_id "
                        + "WHERE p.product_name = ? "
                        + "AND s.shop_name = ? "
                        + "ORDER BY pe.entry_date ASC, "
                        + "pe.price_entry_id ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product);
            ps.setString(2, shop);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date sqlDate = rs.getDate("entry_date");

                    LocalDate date = sqlDate == null
                            ? null
                            : sqlDate.toLocalDate();

                    records.add(new PriceRecord(
                            rs.getInt("price_entry_id"),
                            date,
                            rs.getString("product_name"),
                            rs.getString("shop_name"),
                            rs.getDouble("price")
                    ));
                }
            }
        }

        return records;
    }

    private Map<String, Double> getLatestPricesAcrossShops(
            String product
    ) throws SQLException {

        Map<String, Double> prices = new LinkedHashMap<>();

        String sql =
                "SELECT s.shop_name, pe.price "
                        + "FROM shops s "
                        + "JOIN price_entries pe "
                        + "ON pe.shop_id = s.shop_id "
                        + "JOIN products p "
                        + "ON p.product_id = pe.product_id "
                        + "WHERE p.product_name = ? "
                        + "AND pe.price_entry_id = ("
                        + "SELECT pe2.price_entry_id "
                        + "FROM price_entries pe2 "
                        + "JOIN products p2 "
                        + "ON p2.product_id = pe2.product_id "
                        + "WHERE pe2.shop_id = s.shop_id "
                        + "AND p2.product_name = p.product_name "
                        + "ORDER BY pe2.entry_date DESC, "
                        + "pe2.price_entry_id DESC "
                        + "LIMIT 1"
                        + ") "
                        + "ORDER BY s.shop_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, product);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prices.put(
                            rs.getString("shop_name"),
                            rs.getDouble("price")
                    );
                }
            }
        }

        return prices;
    }

    private void updateSummary(List<PriceRecord> history) {
        if (history.isEmpty()) {
            latestValue.setText("—");
            previousValue.setText("—");
            trendValue.setText("—");
            changeValue.setText("—");
            return;
        }

        PriceRecord latest = history.get(history.size() - 1);

        latestValue.setText("₹" + MONEY.format(latest.price));

        if (history.size() < 2) {
            previousValue.setText("—");
            trendValue.setText("Not enough data");
            changeValue.setText("—");
            return;
        }

        PriceRecord previous = history.get(history.size() - 2);

        previousValue.setText("₹" + MONEY.format(previous.price));

        double difference = latest.price - previous.price;

        if (difference > 0.0001) {
            trendValue.setText("Increasing");
            trendValue.setForeground(new Color(190, 75, 65));
        } else if (difference < -0.0001) {
            trendValue.setText("Decreasing");
            trendValue.setForeground(new Color(45, 145, 95));
        } else {
            trendValue.setText("Stable");
            trendValue.setForeground(DARK);
        }

        if (Math.abs(previous.price) < 0.0001) {
            changeValue.setText("N/A");
        } else {
            double percentage =
                    (difference / previous.price) * 100.0;

            changeValue.setText(
                    (percentage > 0 ? "+" : "")
                            + MONEY.format(percentage) + "%"
            );
        }
    }

    private void updateHistoryTable(List<PriceRecord> history) {
        tableModel.setRowCount(0);

        for (int i = history.size() - 1; i >= 0; i--) {
            PriceRecord record = history.get(i);

            String date = record.date == null
                    ? "—"
                    : record.date.format(DATE_FORMAT);

            tableModel.addRow(new Object[]{
                    date,
                    record.product,
                    record.shop,
                    MONEY.format(record.price)
            });
        }
    }

    private static class PriceRecord {
        final int id;
        final LocalDate date;
        final String product;
        final String shop;
        final double price;

        PriceRecord(
                int id,
                LocalDate date,
                String product,
                String shop,
                double price
        ) {
            this.id = id;
            this.date = date;
            this.product = product;
            this.shop = shop;
            this.price = price;
        }
    }

    // LINE GRAPH
    private class PriceLineChart extends JPanel {

        private List<PriceRecord> data = new ArrayList<>();

        PriceLineChart() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(600, 240));
            setMinimumSize(new Dimension(200, 200));
        }

        void setData(List<PriceRecord> data) {
            this.data = new ArrayList<>(data);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            int left = 58;
            int right = 22;
            int top = 20;
            int bottom = 42;

            int chartWidth = width - left - right;
            int chartHeight = height - top - bottom;

            if (data.isEmpty()) {
                drawMessage(g2, "No price history to display",
                        width, height);
                g2.dispose();
                return;
            }

            double min = data.stream()
                    .mapToDouble(r -> r.price)
                    .min().orElse(0);

            double max = data.stream()
                    .mapToDouble(r -> r.price)
                    .max().orElse(1);

            if (Math.abs(max - min) < 0.0001) {
                min = Math.max(0, min - 1);
                max = max + 1;
            }

            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));

            // Horizontal grid lines and price labels.
            for (int i = 0; i <= 4; i++) {
                int y = top + (chartHeight * i / 4);
                double value = max - (max - min) * i / 4.0;

                g2.setColor(BORDER);
                g2.drawLine(left, y, width - right, y);

                g2.setColor(MUTED);
                g2.drawString(
                        MONEY.format(value),
                        4,
                        y + 4
                );
            }

            g2.setColor(MUTED);
            g2.drawLine(left, top, left, top + chartHeight);
            g2.drawLine(left, top + chartHeight,
                    width - right, top + chartHeight);

            if (data.size() == 1) {
                int x = left + chartWidth / 2;
                int y = top + (int) (
                        (max - data.get(0).price)
                                / (max - min) * chartHeight
                );

                g2.setColor(new Color(60, 150, 115));
                g2.fillOval(x - 5, y - 5, 10, 10);

                g2.setColor(DARK);
                g2.drawString("1 record", x - 20,
                        top + chartHeight + 22);

                g2.dispose();
                return;
            }

            int count = data.size();
            int previousX = 0;
            int previousY = 0;

            for (int i = 0; i < count; i++) {
                PriceRecord record = data.get(i);

                int x = left + (int) (
                        (double) i / (count - 1) * chartWidth
                );

                int y = top + (int) (
                        (max - record.price)
                                / (max - min) * chartHeight
                );

                if (i > 0) {
                    g2.setColor(new Color(60, 150, 115));
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(previousX, previousY, x, y);
                }

                g2.setColor(new Color(60, 150, 115));
                g2.fillOval(x - 4, y - 4, 8, 8);

                // Show only a few date labels if there are many records.
                int labelStep = Math.max(1, (count - 1) / 4);

                if (i % labelStep == 0 || i == count - 1) {
                    String label = record.date == null
                            ? ""
                            : record.date.format(
                                    DateTimeFormatter.ofPattern("dd MMM")
                            );

                    g2.setColor(MUTED);
                    FontMetrics fm = g2.getFontMetrics();

                    int labelX = Math.max(
                            left,
                            Math.min(
                                    x - fm.stringWidth(label) / 2,
                                    width - right - fm.stringWidth(label)
                            )
                    );

                    g2.drawString(
                            label,
                            labelX,
                            top + chartHeight + 22
                    );
                }

                previousX = x;
                previousY = y;
            }

            g2.dispose();
        }
    }

    // BAR GRAPH
    private class ShopBarChart extends JPanel {

        private Map<String, Double> data = new LinkedHashMap<>();

        ShopBarChart() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(600, 240));
            setMinimumSize(new Dimension(200, 200));
        }

        void setData(Map<String, Double> data) {
            this.data = new LinkedHashMap<>(data);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            if (data.isEmpty()) {
                drawMessage(g2, "No shop prices to compare",
                        width, height);
                g2.dispose();
                return;
            }

            int left = 55;
            int right = 18;
            int top = 20;
            int bottom = 45;

            int chartWidth = width - left - right;
            int chartHeight = height - top - bottom;

            double max = data.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .max().orElse(1);

            if (max <= 0) {
                max = 1;
            }

            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));

            for (int i = 0; i <= 4; i++) {
                int y = top + (chartHeight * i / 4);
                double value = max * (4 - i) / 4.0;

                g2.setColor(BORDER);
                g2.drawLine(left, y, width - right, y);

                g2.setColor(MUTED);
                g2.drawString(MONEY.format(value), 4, y + 4);
            }

            int count = data.size();
            int slotWidth = Math.max(1, chartWidth / count);
            int barWidth = Math.max(8, Math.min(55, slotWidth / 2));

            int index = 0;

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int centerX = left + slotWidth * index + slotWidth / 2;

                int barHeight = (int) (
                        entry.getValue() / max * chartHeight
                );

                int barX = centerX - barWidth / 2;
                int barY = top + chartHeight - barHeight;

                g2.setColor(new Color(135, 202, 171));
                g2.fillRoundRect(
                        barX, barY, barWidth,
                        Math.max(1, barHeight), 8, 8
                );

                g2.setColor(DARK);
                String priceLabel = MONEY.format(entry.getValue());
                FontMetrics fm = g2.getFontMetrics();

                int priceX = centerX - fm.stringWidth(priceLabel) / 2;
                g2.drawString(priceLabel, priceX,
                        Math.max(top + 12, barY - 5));

                String shopName = entry.getKey();
                int maxLabelWidth = Math.max(35, slotWidth - 4);

                // Shorten long shop names to fit below the bars.
                while (shopName.length() > 3
                        && fm.stringWidth(shopName) > maxLabelWidth) {
                    shopName = shopName.substring(
                            0, shopName.length() - 2
                    ) + "…";
                }

                int labelX = centerX - fm.stringWidth(shopName) / 2;

                g2.setColor(MUTED);
                g2.drawString(
                        shopName,
                        Math.max(left, labelX),
                        top + chartHeight + 20
                );

                index++;
            }

            g2.dispose();
        }
    }

    private void drawMessage(
            Graphics2D g2,
            String message,
            int width,
            int height
    ) {
        g2.setColor(MUTED);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));

        FontMetrics fm = g2.getFontMetrics();

        g2.drawString(
                message,
                Math.max(10, (width - fm.stringWidth(message)) / 2),
                height / 2
        );
    }
}

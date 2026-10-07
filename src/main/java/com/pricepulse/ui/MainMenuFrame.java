package com.pricepulse.ui;

import com.pricepulse.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainMenuFrame extends JFrame {

    private final JTabbedPane tabs = new JTabbedPane();

    // =========================
    // COLORS
    // =========================

    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color BACKGROUND = new Color(248, 250, 252);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);

    public MainMenuFrame(User user) {

        super("PricePulse");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);

        // =========================
        // TABS
        // =========================

        tabs.setFont(new Font("Arial", Font.PLAIN, 14));
        tabs.setBackground(Color.WHITE);

        // =========================
        // HOME DASHBOARD
        // =========================

        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBackground(BACKGROUND);
        dashboard.setBorder(new EmptyBorder(25, 35, 30, 35));

        // =========================
        // HEADER
        // =========================

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD);
        header.setBorder(new EmptyBorder(18, 22, 18, 22));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Brand
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(CARD);

        JLabel logo = new JLabel("PricePulse");
        logo.setFont(new Font("Arial", Font.BOLD, 25));
        logo.setForeground(PRIMARY);

        JLabel tagline = new JLabel("Smart Price Tracking");
        tagline.setFont(new Font("Arial", Font.PLAIN, 13));
        tagline.setForeground(MUTED);

        brandPanel.add(logo);
        brandPanel.add(Box.createVerticalStrut(3));
        brandPanel.add(tagline);

        // User information
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(CARD);

        JLabel welcome = new JLabel("Welcome, " + user.getName());
        welcome.setFont(new Font("Arial", Font.BOLD, 15));
        welcome.setForeground(TEXT);
        welcome.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel status = new JLabel("Logged in");
        status.setFont(new Font("Arial", Font.PLAIN, 12));
        status.setForeground(MUTED);
        status.setAlignmentX(Component.RIGHT_ALIGNMENT);

        userPanel.add(welcome);
        userPanel.add(Box.createVerticalStrut(3));
        userPanel.add(status);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);

        dashboard.add(header);

        dashboard.add(Box.createVerticalStrut(25));

        // =========================
        // DASHBOARD TITLE
        // =========================

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboard.add(title);

        dashboard.add(Box.createVerticalStrut(5));

        JLabel description = new JLabel(
                "Manage products, shops and prices from one place."
        );
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setForeground(MUTED);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboard.add(description);

        dashboard.add(Box.createVerticalStrut(22));

        // =========================
        // STATISTICS
        // =========================

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(BACKGROUND);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 82));
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsPanel.add(createStatCard("Products", "12"));
        statsPanel.add(createStatCard("Shops", "8"));
        statsPanel.add(createStatCard("Price Entries", "46"));

        dashboard.add(statsPanel);

        dashboard.add(Box.createVerticalStrut(28));

        // =========================
        // QUICK ACTIONS
        // =========================

        JLabel quickTitle = new JLabel("Quick Actions");
        quickTitle.setFont(new Font("Arial", Font.BOLD, 20));
        quickTitle.setForeground(TEXT);
        quickTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboard.add(quickTitle);

        dashboard.add(Box.createVerticalStrut(12));

        // =========================
        // ACTION CARDS
        // =========================

        JPanel actionsPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        actionsPanel.setBackground(BACKGROUND);
        actionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        actionsPanel.add(createActionCard(
                "Products",
                "Add or manage products",
                "Products"
        ));

        actionsPanel.add(createActionCard(
                "Shops",
                "Add or manage shops",
                "Shops"
        ));

        actionsPanel.add(createActionCard(
                "Price Entries",
                "Record and view prices",
                "Price Entries"
        ));

        actionsPanel.add(createActionCard(
                "Price Trends",
                "View price changes",
                "Price Trends"
        ));

        dashboard.add(actionsPanel);

        dashboard.add(Box.createVerticalStrut(15));

        // =========================
        // FIND CHEAPEST SHOP
        // =========================

        JPanel cheapestShop = createLargeActionCard(
                "Find Cheapest Shop",
                "Compare prices and find the best shop for a product.",
                "Cheapest Shop"
        );

        cheapestShop.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboard.add(cheapestShop);

        dashboard.add(Box.createVerticalStrut(15));

        // =========================
        // SCROLL PANE
        // =========================

        JScrollPane scrollPane = new JScrollPane(dashboard);

        scrollPane.setBorder(null);

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // =========================
        // HOME TAB
        // =========================

        tabs.addTab("Home", scrollPane);

        add(tabs, BorderLayout.CENTER);
    }

    // =====================================================
    // STAT CARD
    // =====================================================

    private JPanel createStatCard(String title, String value) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 16, 12, 16)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setForeground(MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 23));
        valueLabel.setForeground(TEXT);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.SOUTH);

        return card;
    }

    // =====================================================
    // ACTION CARD
    // =====================================================

    private JPanel createActionCard(
            String title,
            String description,
            String moduleName
    ) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(16, 18, 16, 15)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(CARD);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionLabel.setForeground(MUTED);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descriptionLabel);

        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("Arial", Font.BOLD, 25));
        arrow.setForeground(PRIMARY);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // =========================
        // HOVER + CLICK
        // =========================

        card.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {

                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY),
                        new EmptyBorder(16, 18, 16, 15)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {

                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(16, 18, 16, 15)
                ));
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                showComingSoon(moduleName);
            }
        });

        return card;
    }

    // =====================================================
    // LARGE ACTION CARD
    // =====================================================

    private JPanel createLargeActionCard(
            String title,
            String description,
            String moduleName
    ) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(17, 20, 17, 20)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(CARD);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        descriptionLabel.setForeground(MUTED);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descriptionLabel);

        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("Arial", Font.BOLD, 28));
        arrow.setForeground(PRIMARY);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(arrow, BorderLayout.EAST);

        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // =========================
        // HOVER + CLICK
        // =========================

        card.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {

                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY),
                        new EmptyBorder(17, 20, 17, 20)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {

                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(17, 20, 17, 20)
                ));
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                showComingSoon(moduleName);
            }
        });

        return card;
    }

    // =====================================================
    // TEMPORARY MESSAGE
    // =====================================================

    private void showComingSoon(String module) {

        JOptionPane.showMessageDialog(
                this,
                module + " module is not connected yet.",
                "PricePulse",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // MODULE INTEGRATION
    // =====================================================

    public void addModuleTab(String title, JPanel panel) {
        tabs.addTab(title, panel);
    }
}
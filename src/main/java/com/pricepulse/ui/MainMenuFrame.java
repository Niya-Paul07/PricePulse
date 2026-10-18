
package com.pricepulse.ui;

import com.pricepulse.ItemService;
import com.pricepulse.ShopService;
import com.pricepulse.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenuFrame extends JFrame {

    private final JTabbedPane tabs = new JTabbedPane();

    // Shared services used by all modules
    private final ItemService itemService = new ItemService();
    private final ShopService shopService = new ShopService();

    // PricePulse colour palette
    private static final Color GREEN = new Color(32, 112, 81);
    private static final Color DARK_GREEN = new Color(25, 67, 52);
    private static final Color BACKGROUND = new Color(247, 250, 246);
    private static final Color MINT = new Color(222, 241, 226);
    private static final Color CREAM = new Color(250, 246, 230);
    private static final Color TEXT = new Color(38, 59, 48);
    private static final Color MUTED = new Color(108, 128, 115);
    private static final Color BORDER = new Color(225, 235, 226);
    private static final Color WHITE = Color.WHITE;

    public MainMenuFrame(User user) {
        super("PricePulse | Smart Price Tracking");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 740);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND);

        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.setBackground(WHITE);
        tabs.setForeground(TEXT);

        tabs.addTab("  Home  ", buildDashboard(user));

        add(tabs, BorderLayout.CENTER);
    }

    // DASHBOARD
    private JPanel buildDashboard(User user) {

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(BACKGROUND);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(new EmptyBorder(25, 30, 30, 30));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JPanel brand = new JPanel();
        brand.setOpaque(false);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("PricePulse");
        logo.setFont(new Font("SansSerif", Font.BOLD, 28));
        logo.setForeground(DARK_GREEN);

        JLabel tagline = new JLabel("Better prices. Smarter choices.");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tagline.setForeground(MUTED);

        brand.add(logo);
        brand.add(Box.createVerticalStrut(3));
        brand.add(tagline);

        String username = user != null && user.getName() != null
                ? user.getName() : "User";

        JPanel userInfo = new JPanel();
        userInfo.setOpaque(false);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome, " + username);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 15));
        welcome.setForeground(TEXT);
        welcome.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel status = new JLabel("Smart shopping starts here");
        status.setFont(new Font("SansSerif", Font.PLAIN, 12));
        status.setForeground(MUTED);
        status.setAlignmentX(Component.RIGHT_ALIGNMENT);

        userInfo.add(welcome);
        userInfo.add(Box.createVerticalStrut(4));
        userInfo.add(status);

        header.add(brand, BorderLayout.WEST);
        header.add(userInfo, BorderLayout.EAST);

        content.add(header);
        content.add(Box.createVerticalStrut(25));

        // HERO BANNER
        GradientPanel hero = new GradientPanel();
        hero.setLayout(new BorderLayout(15, 10));
        hero.setBorder(new EmptyBorder(25, 28, 25, 25));
        hero.setPreferredSize(new Dimension(900, 220));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("COMPARE  •  TRACK  •  SAVE");
        eyebrow.setFont(new Font("SansSerif", Font.BOLD, 11));
        eyebrow.setForeground(GREEN);
        eyebrow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heroTitle = new JLabel(
                "<html>Find better prices for<br>"
                + "your everyday essentials</html>");
        heroTitle.setFont(new Font("SansSerif", Font.BOLD, 25));
        heroTitle.setForeground(DARK_GREEN);
        heroTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heroDescription = new JLabel(
                "<html>Track product prices, compare shops and<br>"
                + "make smarter shopping decisions.</html>");
        heroDescription.setFont(new Font("SansSerif", Font.PLAIN, 13));
        heroDescription.setForeground(TEXT);
        heroDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton addProductButton = new JButton("+ Add Product");
        styleButton(addProductButton);
        addProductButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        addProductButton.addActionListener(e -> openProducts());

        heroText.add(eyebrow);
        heroText.add(Box.createVerticalStrut(8));
        heroText.add(heroTitle);
        heroText.add(Box.createVerticalStrut(7));
        heroText.add(heroDescription);
        heroText.add(Box.createVerticalStrut(13));
        heroText.add(addProductButton);

        JLabel artwork = new JLabel(
                "<html><div style='text-align:center;'>"
                + "<font size='7'>🥬</font> "
                + "<font size='7'>🍅</font><br>"
                + "<font size='6'>🥑 &nbsp; 🥕</font>"
                + "</div></html>");
        artwork.setHorizontalAlignment(SwingConstants.CENTER);
        artwork.setPreferredSize(new Dimension(190, 140));

        hero.add(heroText, BorderLayout.CENTER);
        hero.add(artwork, BorderLayout.EAST);

        content.add(hero);
        content.add(Box.createVerticalStrut(27));

        // QUICK ACTIONS
        JLabel sectionTitle = new JLabel("Explore PricePulse");
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 21));
        sectionTitle.setForeground(TEXT);
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sectionDescription = new JLabel(
                "Everything you need to make smarter price decisions.");
        sectionDescription.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sectionDescription.setForeground(MUTED);
        sectionDescription.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(sectionTitle);
        content.add(Box.createVerticalStrut(5));
        content.add(sectionDescription);
        content.add(Box.createVerticalStrut(16));

        JPanel cards = new JPanel(new GridLayout(0, 2, 16, 16));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);

        cards.add(createActionCard(
                "📦",
                "Add Product",
                "Add or manage products in your price tracker.",
                this::openProducts));

        cards.add(createActionCard(
                "🏪",
                "Register Shop",
                "Register, view and manage shops for price comparison.",
                this::openShops));

        cards.add(createActionCard(
                "🔎",
                "Find Cheapest",
                "Compare prices and discover the best deal.",
                this::openCheapestShop));

        cards.add(createActionCard(
                "📈",
                "Price Trends",
                "Explore how product prices change over time.",
                () -> showComingSoon("Price Trends")));

        cards.add(createActionCard(
                "🔔",
                "Price Alerts",
                "Keep track of prices you want to watch.",
                () -> showComingSoon("Price Alerts")));

        content.add(cards);
        content.add(Box.createVerticalStrut(23));

        JLabel footer = new JLabel(
                "Little savings today. Smarter choices tomorrow. 🌿");
        footer.setFont(new Font("SansSerif", Font.ITALIC, 13));
        footer.setForeground(MUTED);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(footer);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(BACKGROUND);
        scroll.getViewport().setBackground(BACKGROUND);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        page.add(scroll, BorderLayout.CENTER);

        return page;
    }

    // REUSABLE ACTION CARD
    private JPanel createActionCard(
            String icon,
            String title,
            String description,
            Runnable action) {

        RoundedPanel card = new RoundedPanel(20, WHITE);
        card.setLayout(new BorderLayout(12, 5));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));
        card.setPreferredSize(new Dimension(330, 130));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 27));
        iconLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT);

        JLabel descriptionLabel = new JLabel(
                "<html><div style='width:235px'>"
                + description + "</div></html>");
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descriptionLabel.setForeground(MUTED);

        JLabel explore = new JLabel("Explore  →");
        explore.setFont(new Font("SansSerif", Font.BOLD, 12));
        explore.setForeground(GREEN);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(7));
        textPanel.add(descriptionLabel);
        textPanel.add(Box.createVerticalStrut(12));
        textPanel.add(explore);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setCardColor(MINT);
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setCardColor(WHITE);
                card.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });

        return card;
    }

    // OPEN PRODUCT MODULE
    private void openProducts() {
        ProductPanel panel =
                new ProductPanel(itemService, shopService);

        addModuleTab("Products", panel);
    }

    // OPEN SHOP REGISTRATION MODULE
    private void openShops() {
        ShopPanel panel = new ShopPanel(shopService);
        addModuleTab("Shops", panel);
    }

    // OPEN FIND CHEAPEST MODULE
    private void openCheapestShop() {
        CheapestShopPanel panel =
                new CheapestShopPanel(itemService, shopService);

        addModuleTab("Find Cheapest", panel);
    }

    // PLACEHOLDER FOR MODULES NOT YET INTEGRATED
    private void showComingSoon(String module) {
        JOptionPane.showMessageDialog(
                this,
                module + " will be connected when its module is integrated.",
                "PricePulse",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // REUSABLE TAB METHOD
    public void addModuleTab(String title, JPanel panel) {
        for (int i = 0; i < tabs.getTabCount(); i++) {
            if (tabs.getTitleAt(i).equals(title)) {
                tabs.setSelectedIndex(i);
                return;
            }
        }

        tabs.addTab(title, panel);
        tabs.setSelectedComponent(panel);
    }

    // BUTTON STYLE
    private void styleButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(WHITE);
        button.setBackground(GREEN);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 17, 10, 17));
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));
    }

    // MINT-TO-CREAM GRADIENT
    private static class GradientPanel extends JPanel {

        GradientPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(215, 239, 221),
                    getWidth(), getHeight(), CREAM);

            g2.setPaint(gradient);
            g2.fillRoundRect(
                    0, 0, getWidth(), getHeight(), 26, 26);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ROUNDED CARD WITH SUBTLE SHADOW
    private static class RoundedPanel extends JPanel {

        private Color cardColor;
        private final int radius;

        RoundedPanel(int radius, Color color) {
            this.radius = radius;
            this.cardColor = color;
            setOpaque(false);
        }

        void setCardColor(Color color) {
            cardColor = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(35, 70, 48, 15));
            g2.fillRoundRect(
                    2, 3, getWidth() - 5, getHeight() - 5,
                    radius, radius);

            g2.setColor(cardColor);
            g2.fillRoundRect(
                    0, 0, getWidth() - 5, getHeight() - 6,
                    radius, radius);

            g2.setColor(BORDER);
            g2.drawRoundRect(
                    0, 0, getWidth() - 5, getHeight() - 6,
                    radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}

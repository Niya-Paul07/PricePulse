
package com.pricepulse.ui;

import com.pricepulse.auth.AuthService;
import com.pricepulse.model.User;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.function.Consumer;

public class AuthWindow extends JFrame {

    private static final Color CREAM = new Color(248, 248, 237);
    private static final Color CARD = new Color(255, 255, 250);
    private static final Color GREEN = new Color(73, 119, 82);
    private static final Color DARK_GREEN = new Color(36, 75, 51);
    private static final Color MUTED = new Color(112, 133, 115);
    private static final Color BORDER = new Color(218, 225, 211);
    private static final Color RED = new Color(185, 65, 65);

    private final AuthService authService;
    private final Consumer<User> onSuccess;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    private final JTextField loginEmail = new JTextField();
    private final JPasswordField loginPassword = new JPasswordField();
    private final JLabel loginStatus = new JLabel(" ");

    private final JTextField suName = new JTextField();
    private final JTextField suEmail = new JTextField();
    private final JPasswordField suPassword = new JPasswordField();
    private final JLabel suStatus = new JLabel(" ");

    private final JButton loginTab = new JButton("Login");
    private final JButton signUpTab = new JButton("Sign Up");

    public AuthWindow(AuthService authService, Consumer<User> onSuccess) {
        super("PricePulse | Welcome");

        this.authService = authService;
        this.onSuccess = onSuccess;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(850, 570));
        setSize(1050, 650);
        setLocationRelativeTo(null);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(CREAM);
        background.setBorder(new EmptyBorder(22, 28, 22, 28));

        JPanel content = new JPanel(new GridLayout(1, 2, 35, 0));
        content.setOpaque(false);

        content.add(buildBrandPanel());
        content.add(buildAuthPanel());

        background.add(content);
        setContentPane(background);

        showLogin();
    }

    // ---------------- BRANDING ----------------

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(18, 8, 12, 8));

        JPanel branding = new JPanel();
        branding.setOpaque(false);
        branding.setLayout(new BoxLayout(branding, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("PricePulse");
        logo.setFont(new Font("SansSerif", Font.BOLD, 43));
        logo.setForeground(DARK_GREEN);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Better prices. Fresher choices.");
        tagline.setFont(new Font("SansSerif", Font.PLAIN, 17));
        tagline.setForeground(MUTED);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(
                "<html>Compare everyday prices and make<br>"
                + "smarter shopping decisions.</html>");
        description.setFont(new Font("SansSerif", Font.PLAIN, 14));
        description.setForeground(MUTED);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        branding.add(logo);
        branding.add(Box.createVerticalStrut(8));
        branding.add(tagline);
        branding.add(Box.createVerticalStrut(17));
        branding.add(description);

        VegetableIllustration illustration = new VegetableIllustration();
        illustration.setOpaque(false);
        illustration.setPreferredSize(new Dimension(390, 300));

        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));

        JLabel footerTitle = new JLabel("Fresh choices. Smarter savings.");
        footerTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        footerTitle.setForeground(GREEN);
        footerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel footerText = new JLabel("Your everyday price companion.");
        footerText.setFont(new Font("SansSerif", Font.PLAIN, 13));
        footerText.setForeground(MUTED);
        footerText.setAlignmentX(Component.LEFT_ALIGNMENT);

        footer.add(footerTitle);
        footer.add(Box.createVerticalStrut(5));
        footer.add(footerText);

        panel.add(branding, BorderLayout.NORTH);
        panel.add(illustration, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    // ---------------- AUTHENTICATION CARD ----------------

    private JPanel buildAuthPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        RoundedPanel card = new RoundedPanel(24, CARD);
        card.setLayout(new BorderLayout(0, 20));
        card.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel tabs = new JPanel(new GridLayout(1, 2, 14, 0));
        tabs.setOpaque(false);

        styleTab(loginTab, true);
        styleTab(signUpTab, false);

        loginTab.addActionListener(e -> showLogin());
        signUpTab.addActionListener(e -> showSignUp());

        tabs.add(loginTab);
        tabs.add(signUpTab);

        cards.setOpaque(false);
        cards.add(buildLoginPanel(), "LOGIN");
        cards.add(buildSignUpPanel(), "SIGNUP");

        card.add(tabs, BorderLayout.NORTH);
        card.add(cards, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        wrapper.add(card, gbc);

        return wrapper;
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel heading = heading("Welcome back!");
        JLabel subtitle = subtitle("Sign in to your PricePulse account");

        styleField(loginEmail, "Email address");
        styleField(loginPassword, "Password");
        styleStatus(loginStatus);

        JButton loginButton = primaryButton("Sign In");
        loginButton.addActionListener(this::handleLogin);

        panel.add(Box.createVerticalStrut(10));
        panel.add(heading);
        panel.add(Box.createVerticalStrut(7));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(25));

        panel.add(fieldLabel("Email address"));
        panel.add(Box.createVerticalStrut(7));
        panel.add(loginEmail);
        panel.add(Box.createVerticalStrut(15));

        panel.add(fieldLabel("Password"));
        panel.add(Box.createVerticalStrut(7));
        panel.add(loginPassword);
        panel.add(Box.createVerticalStrut(8));
        panel.add(loginStatus);
        panel.add(Box.createVerticalStrut(12));

        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(16));
        panel.add(centeredText("Your everyday shopping, made smarter."));

        return panel;
    }

    private JPanel buildSignUpPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = heading("Create your account");
        JLabel description = subtitle("Join PricePulse and shop smarter");

        styleField(suName, "Full name");
        styleField(suEmail, "Email address");
        styleField(suPassword, "Password");
        styleStatus(suStatus);

        JButton createButton = primaryButton("Create Account");
        createButton.addActionListener(this::handleSignUp);

        panel.add(Box.createVerticalStrut(3));
        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(description);
        panel.add(Box.createVerticalStrut(17));

        panel.add(fieldLabel("Full name"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(suName);
        panel.add(Box.createVerticalStrut(9));

        panel.add(fieldLabel("Email address"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(suEmail);
        panel.add(Box.createVerticalStrut(9));

        panel.add(fieldLabel("Password"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(suPassword);
        panel.add(Box.createVerticalStrut(5));

        panel.add(suStatus);
        panel.add(Box.createVerticalStrut(8));
        panel.add(createButton);

        return panel;
    }

    // ---------------- STYLING ----------------

    private JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 27));
        label.setForeground(DARK_GREEN);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(DARK_GREEN);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel centeredText(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(MUTED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void styleField(JTextField field, String tooltip) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(DARK_GREEN);
        field.setBackground(new Color(250, 251, 245));
        field.setCaretColor(GREEN);
        field.setToolTipText(tooltip);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 13),
                new EmptyBorder(9, 13, 9, 13)));
        field.setPreferredSize(new Dimension(300, 44));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleStatus(JLabel label) {
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(RED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JButton primaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(isEnabled() ? GREEN : MUTED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(300, 47));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 47));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        return button;
    }

    private void styleTab(JButton button, boolean selected) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(selected ? DARK_GREEN : MUTED);
        button.setBackground(CARD);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createMatteBorder(
                0, 0, selected ? 3 : 1, 0,
                selected ? GREEN : BORDER));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
    }

    private void showLogin() {
        cardLayout.show(cards, "LOGIN");
        styleTab(loginTab, true);
        styleTab(signUpTab, false);
        getRootPane().setDefaultButton(null);
    }

    private void showSignUp() {
        cardLayout.show(cards, "SIGNUP");
        styleTab(loginTab, false);
        styleTab(signUpTab, true);
        getRootPane().setDefaultButton(null);
    }

    // ---------------- LOGIN / SIGNUP LOGIC ----------------

    private void handleLogin(ActionEvent e) {
        String email = loginEmail.getText().trim();
        String password = new String(loginPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            loginStatus.setText("Enter both email and password.");
            return;
        }

        try {
            User user = authService.login(email, password);

            if (user == null) {
                loginStatus.setText("Incorrect email or password.");
                return;
            }

            dispose();
            onSuccess.accept(user);

        } catch (RuntimeException ex) {
            showDatabaseError(ex);
        }
    }

    private void handleSignUp(ActionEvent e) {
        String name = suName.getText().trim();
        String email = suEmail.getText().trim();
        String password = new String(suPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            suStatus.setText("Please fill in all fields.");
            return;
        }

        try {
            User user = authService.signUp(name, email, password);

            if (user == null) {
                suStatus.setText("That email is already registered.");
                return;
            }

            dispose();
            onSuccess.accept(user);

        } catch (RuntimeException ex) {
            showDatabaseError(ex);
        }
    }

    private void showDatabaseError(RuntimeException ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                "We couldn't complete your request.\n"
                + "Please check your database connection and try again.",
                "PricePulse",
                JOptionPane.ERROR_MESSAGE);
    }

    // ---------------- CUSTOM ROUNDED COMPONENTS ----------------

    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g,
                                int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(
                    x + 1, y + 1,
                    width - 3, height - 3,
                    radius, radius);

            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color fill;

        RoundedPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(45, 65, 42, 15));
            g2.fillRoundRect(3, 5,
                    getWidth() - 6, getHeight() - 8, radius, radius);

            g2.setColor(fill);
            g2.fillRoundRect(0, 0,
                    getWidth() - 6, getHeight() - 8, radius, radius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------------- VEGETABLE CRATE ILLUSTRATION ----------------

    private static class VegetableIllustration extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            double scale = Math.min(
                    getWidth() / 390.0,
                    getHeight() / 300.0);

            double offsetX = (getWidth() - 390 * scale) / 2.0;
            double offsetY = (getHeight() - 300 * scale) / 2.0;

            g2.translate(offsetX, offsetY);
            g2.scale(scale, scale);

            // Ground shadow
            g2.setColor(new Color(220, 228, 207, 150));
            g2.fillOval(35, 246, 320, 28);

            // Back of the wooden crate
            g2.setColor(new Color(195, 139, 85));
            g2.fillRect(48, 166, 294, 67);

            g2.setColor(new Color(169, 115, 69));
            g2.drawRect(48, 166, 294, 67);

            // Leafy greens: stems are attached to the crate contents.
            g2.setColor(new Color(65, 108, 62));
            g2.setStroke(new BasicStroke(
                    5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.drawLine(126, 190, 104, 120);
            g2.drawLine(145, 190, 145, 105);
            g2.drawLine(166, 190, 185, 112);
            g2.drawLine(190, 190, 211, 126);

            drawLeaf(g2, 103, 130, -35, 29, new Color(75, 121, 69));
            drawLeaf(g2, 122, 116, 30, 31, new Color(95, 143, 79));
            drawLeaf(g2, 145, 111, -12, 33, new Color(63, 105, 61));
            drawLeaf(g2, 174, 121, 35, 31, new Color(83, 130, 69));
            drawLeaf(g2, 193, 132, -28, 29, new Color(96, 142, 78));

            // Carrots grow from the crate, with leafy tops attached.
            drawCarrot(g2, 214, 108, 29, 83);
            drawCarrot(g2, 244, 119, 26, 72);

            drawLeaf(g2, 218, 111, -38, 23, new Color(70, 119, 63));
            drawLeaf(g2, 226, 107, 22, 23, new Color(91, 139, 73));
            drawLeaf(g2, 246, 122, -30, 21, new Color(75, 125, 66));
            drawLeaf(g2, 253, 119, 25, 21, new Color(96, 145, 76));

            // Tomatoes resting inside the crate.
            drawTomato(g2, 91, 151, 43);
            drawTomato(g2, 135, 158, 41);
            drawTomato(g2, 177, 153, 42);

            // Cucumber resting across the produce.
            g2.setColor(new Color(61, 111, 62));
            g2.fillRoundRect(270, 166, 59, 21, 11, 11);
            g2.setColor(new Color(105, 149, 79));
            g2.drawLine(281, 172, 287, 179);
            g2.drawLine(296, 170, 302, 177);
            g2.drawLine(311, 172, 317, 178);

            // Front crate boards overlap the bottom of the produce.
            g2.setColor(new Color(224, 177, 119));
            g2.fillRoundRect(42, 190, 310, 19, 4, 4);
            g2.fillRoundRect(38, 214, 318, 19, 4, 4);
            g2.fillRoundRect(33, 238, 328, 15, 4, 4);

            g2.setColor(new Color(168, 115, 68));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(42, 190, 310, 19, 4, 4);
            g2.drawRoundRect(38, 214, 318, 19, 4, 4);
            g2.drawRoundRect(33, 238, 328, 15, 4, 4);

            // Small wood details.
            g2.setColor(new Color(164, 111, 66));
            g2.fillOval(51, 197, 5, 5);
            g2.fillOval(334, 197, 5, 5);
            g2.fillOval(47, 221, 5, 5);
            g2.fillOval(339, 221, 5, 5);
            g2.fillOval(44, 243, 5, 5);
            g2.fillOval(345, 243, 5, 5);

            g2.dispose();
        }

        private void drawCarrot(Graphics2D g2, int x, int y,
                                int width, int height) {
            Polygon carrot = new Polygon();
            carrot.addPoint(x, y);
            carrot.addPoint(x + width, y);
            carrot.addPoint(x + width - 5, y + height - 12);
            carrot.addPoint(x + width / 2, y + height);
            carrot.addPoint(x + 5, y + height - 12);

            g2.setColor(new Color(235, 137, 55));
            g2.fillPolygon(carrot);

            g2.setColor(new Color(193, 99, 37));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawLine(x + 7, y + 22, x + 16, y + 26);
            g2.drawLine(x + 12, y + 42, x + 22, y + 46);
            g2.drawLine(x + 10, y + 61, x + 18, y + 64);
        }

        private void drawTomato(Graphics2D g2, int x, int y, int size) {
            g2.setColor(new Color(218, 77, 60));
            g2.fillOval(x, y, size, size);

            g2.setColor(new Color(180, 53, 43));
            g2.fillOval(x + size / 2 - 4, y + 4, 8, 8);

            Polygon stem = new Polygon();
            stem.addPoint(x + size / 2, y + 11);
            stem.addPoint(x + size / 2 - 13, y + 4);
            stem.addPoint(x + size / 2 - 7, y + 15);
            stem.addPoint(x + size / 2, y + 8);
            stem.addPoint(x + size / 2 + 7, y + 15);
            stem.addPoint(x + size / 2 + 13, y + 4);

            g2.setColor(new Color(59, 108, 59));
            g2.fillPolygon(stem);
        }

        private void drawLeaf(Graphics2D g2, int x, int y,
                              int angle, int length, Color color) {
            Graphics2D leaf = (Graphics2D) g2.create();
            leaf.translate(x, y);
            leaf.rotate(Math.toRadians(angle));

            leaf.setColor(color);
            leaf.fillOval(-length / 4, -length / 2,
                    length / 2, length);

            leaf.setColor(new Color(221, 235, 202, 190));
            leaf.setStroke(new BasicStroke(1.2f));
            leaf.drawLine(0, -length / 2 + 5, 0, length / 2 - 5);

            leaf.dispose();
        }
    }
}

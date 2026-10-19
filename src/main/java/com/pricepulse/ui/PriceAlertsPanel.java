
package com.pricepulse.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PriceAlertsPanel extends JPanel {

    private final Color GREEN = new Color(32, 112, 81);
    private final Color DARK_GREEN = new Color(25, 67, 52);
    private final Color BACKGROUND = new Color(247, 250, 246);
    private final Color MINT = new Color(222, 241, 226);
    private final Color TEXT = new Color(38, 59, 48);
    private final Color MUTED = new Color(108, 128, 115);
    private final Color WHITE = Color.WHITE;

    public PriceAlertsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setOpaque(false);

        JLabel title = new JLabel("Price Alerts");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        title.setForeground(DARK_GREEN);

        JLabel subtitle = new JLabel(
                "Keep track of prices and save money on your essentials."
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(MUTED);

        heading.add(title);
        heading.add(Box.createVerticalStrut(8));
        heading.add(subtitle);

        JButton createButton = new JButton("+ Create New Alert");
        createButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        createButton.setForeground(WHITE);
        createButton.setBackground(GREEN);
        createButton.setFocusPainted(false);
        createButton.setBorderPainted(false);
        createButton.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        createButton.setPreferredSize(new Dimension(175, 42));

        createButton.addActionListener(e -> showCreateAlertDialog());

        header.add(heading, BorderLayout.WEST);
        header.add(createButton, BorderLayout.EAST);

        // Empty state
        JPanel emptyPanel = new JPanel();
        emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
        emptyPanel.setBackground(WHITE);
        emptyPanel.setBorder(new EmptyBorder(45, 20, 45, 20));

        JLabel icon = new JLabel("🔔");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 42));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyTitle = new JLabel("No price alerts yet");
        emptyTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        emptyTitle.setForeground(TEXT);
        emptyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emptyDescription = new JLabel(
                "Create an alert to track a product at your chosen shop."
        );
        emptyDescription.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emptyDescription.setForeground(MUTED);
        emptyDescription.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton emptyButton = new JButton("Create Your First Alert");
        emptyButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        emptyButton.setForeground(GREEN);
        emptyButton.setBackground(MINT);
        emptyButton.setFocusPainted(false);
        emptyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyButton.addActionListener(e -> showCreateAlertDialog());

        emptyPanel.add(icon);
        emptyPanel.add(Box.createVerticalStrut(15));
        emptyPanel.add(emptyTitle);
        emptyPanel.add(Box.createVerticalStrut(10));
        emptyPanel.add(emptyDescription);
        emptyPanel.add(Box.createVerticalStrut(22));
        emptyPanel.add(emptyButton);

        add(header, BorderLayout.NORTH);
        add(emptyPanel, BorderLayout.CENTER);
    }

    private void showCreateAlertDialog() {

        JTextField productField = new JTextField(18);
        JTextField shopField = new JTextField(18);
        JTextField targetPriceField = new JTextField(18);

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 15));
        form.setBorder(new EmptyBorder(15, 10, 15, 10));
        form.setBackground(WHITE);

        form.add(new JLabel("Product name:"));
        form.add(productField);

        form.add(new JLabel("Shop name:"));
        form.add(shopField);

        form.add(new JLabel("Target price (₹):"));
        form.add(targetPriceField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                "Create Price Alert",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            String product = productField.getText().trim();
            String shop = shopField.getText().trim();
            String target = targetPriceField.getText().trim();

            if (product.isEmpty() || shop.isEmpty() || target.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please fill in all fields.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            try {
                double price = Double.parseDouble(target);

                if (price <= 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Target price must be greater than zero.",
                            "Invalid Price",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Your alert details have been entered!\n\n"
                                + "Product: " + product + "\n"
                                + "Shop: " + shop + "\n"
                                + "Target price: ₹" + price
                                + "\n\nSaving alerts will be added next.",
                        "Price Alert",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid target price.",
                        "Invalid Price",
                        JOptionPane.WARNING_MESSAGE
                );
            }
        }
    }
}

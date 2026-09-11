package com.pricepulse.ui;

import com.pricepulse.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * Main app window shown after login. Uses a tabbed layout so each
 * teammate's module is its own JPanel tab.
 *
 * INTEGRATION TODO (Niya): as each module is ready, add it here with
 * tabs.addTab("Tab Name", somePanel) — one line per module. Suggested tabs:
 *   - "Products & Shops"     -> Devaprabha's CRUD panel
 *   - "Price Entries"        -> Vaishnavi's CRUD panel
 *   - "Cheapest Shop"        -> Shreya's lookup panel
 *   - "Trends"               -> Aswin's trend analysis panel
 *   - "Price Alerts"         -> Devipriya's price-change detection panel
 *
 * Convention for teammates: build your feature as a class that extends
 * JPanel (e.g. `public class ProductShopPanel extends JPanel`), do your
 * layout/logic inside it, and hand it to Niya to add as a tab. That's the
 * only thing this file needs to change per module.
 */
public class MainMenuFrame extends JFrame {
    private final JTabbedPane tabs = new JTabbedPane();

    public MainMenuFrame(User user) {
        super("PricePulse — Welcome, " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JLabel welcome = new JLabel("  Logged in as: " + user.getName() + " (" + user.getEmail() + ")");
        welcome.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(welcome, BorderLayout.NORTH);

        // Placeholder tab until real modules are wired in.
        tabs.addTab("Home", buildPlaceholderTab());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildPlaceholderTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(
            "<html><center>Other modules (CRUD, Lookup, Trends, Alerts)<br>"
            + "will appear here as tabs once merged.</center></html>",
            SwingConstants.CENTER
        );
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    /** Lets teammates' modules be added as tabs during integration. */
    public void addModuleTab(String title, JPanel panel) {
        tabs.addTab(title, panel);
    }
}

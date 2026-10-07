package com.pricepulse.ui;

import com.pricepulse.Shop;
import com.pricepulse.ShopService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ShopPanel extends JPanel {

    // =========================
    // COLORS
    // =========================

    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color BACKGROUND = new Color(248, 250, 252);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);

    // =========================
    // BACKEND
    // =========================

    private final ShopService shopService;

    // =========================
    // UI COMPONENTS
    // =========================

    private JTextField nameField;
    private JTextField locationField;

    private JTable shopTable;
    private DefaultTableModel tableModel;

    private int selectedShopId = -1;

    // =========================
    // CONSTRUCTOR
    // =========================

    public ShopPanel(ShopService shopService) {

        this.shopService = shopService;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(25, 35, 30, 35));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        refreshTable();
    }

    // =========================
    // HEADER
    // =========================

    private JPanel createHeader() {

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BACKGROUND);

        JLabel title = new JLabel("Shops");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(TEXT);

        JLabel description = new JLabel(
                "Add, edit and manage the shops used in PricePulse."
        );
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setForeground(MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(description);
        header.add(Box.createVerticalStrut(20));

        return header;
    }

    // =========================
    // MAIN CONTENT
    // =========================

    private JPanel createMainContent() {

        JPanel mainPanel = new JPanel(new BorderLayout(0, 18));
        mainPanel.setBackground(BACKGROUND);

        mainPanel.add(createFormPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        return mainPanel;
    }

    // =========================
    // FORM PANEL
    // =========================

    private JPanel createFormPanel() {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 20, 18, 20)
        ));

        // -------------------------
        // FORM FIELDS
        // -------------------------

        JPanel fieldsPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        fieldsPanel.setBackground(CARD);

        // Shop Name
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setBackground(CARD);

        JLabel nameLabel = createLabel("Shop Name");
        nameField = new JTextField();

        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 35)
        );

        namePanel.add(nameLabel);
        namePanel.add(Box.createVerticalStrut(6));
        namePanel.add(nameField);

        // Location
        JPanel locationPanel = new JPanel();
        locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
        locationPanel.setBackground(CARD);

        JLabel locationLabel = createLabel("Location");
        locationField = new JTextField();

        locationField.setFont(new Font("Arial", Font.PLAIN, 14));
        locationField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 35)
        );

        locationPanel.add(locationLabel);
        locationPanel.add(Box.createVerticalStrut(6));
        locationPanel.add(locationField);

        fieldsPanel.add(namePanel);
        fieldsPanel.add(locationPanel);

        card.add(fieldsPanel, BorderLayout.CENTER);

        // -------------------------
        // BUTTONS
        // -------------------------

        JPanel buttonPanel = new JPanel(new FlowLayout(
                FlowLayout.LEFT,
                10,
                15
        ));

        buttonPanel.setBackground(CARD);

        JButton addButton = createButton(
                "Add Shop",
                PRIMARY
        );

        JButton updateButton = createButton(
                "Update",
                new Color(71, 85, 105)
        );

        JButton deleteButton = createButton(
                "Delete",
                new Color(220, 38, 38)
        );

        JButton clearButton = createButton(
                "Clear",
                new Color(100, 116, 139)
        );

        addButton.addActionListener(e -> addShop());
        updateButton.addActionListener(e -> updateShop());
        deleteButton.addActionListener(e -> deleteShop());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
    }

    // =========================
    // TABLE PANEL
    // =========================

    private JPanel createTablePanel() {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel tableTitle = new JLabel("Shop List");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tableTitle.setForeground(TEXT);

        tableTitle.setBorder(
                new EmptyBorder(0, 0, 12, 0)
        );

        card.add(tableTitle, BorderLayout.NORTH);

        // -------------------------
        // TABLE
        // -------------------------

        String[] columns = {
                "ID",
                "Shop Name",
                "Location"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        shopTable = new JTable(tableModel);

        shopTable.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        shopTable.setRowHeight(30);

        shopTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        shopTable.getTableHeader().setBackground(
                new Color(241, 245, 249)
        );

        shopTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        // When user selects a shop
        shopTable.getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {
                        selectShop();
                    }
                });

        JScrollPane scrollPane =
                new JScrollPane(shopTable);

        scrollPane.setBorder(null);

        card.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return card;
    }

    // =========================
    // ADD SHOP
    // =========================

    private void addShop() {

        String name = nameField.getText().trim();
        String location = locationField.getText().trim();

        // Validation
        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Shop name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            nameField.requestFocus();
            return;
        }

        if (location.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Shop location is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            locationField.requestFocus();
            return;
        }

        // Create Shop object
        Shop shop = new Shop(
                name,
                location
        );

        // Send to backend
        boolean success =
                shopService.addShop(shop);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Shop added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            refreshTable();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not add shop.\n"
                            + "A shop with this name may already exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // UPDATE SHOP
    // =========================

    private void updateShop() {

        if (selectedShopId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a shop from the table first.",
                    "No Shop Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String name = nameField.getText().trim();
        String location = locationField.getText().trim();

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Shop name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (location.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Shop location is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Shop updatedShop = new Shop(
                selectedShopId,
                name,
                location
        );

        boolean success =
                shopService.updateShop(updatedShop);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Shop updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            refreshTable();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not update shop.\n"
                            + "Another shop may already have this name.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // DELETE SHOP
    // =========================

    private void deleteShop() {

        if (selectedShopId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a shop from the table first.",
                    "No Shop Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this shop?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success =
                shopService.deleteShop(selectedShopId);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Shop deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            refreshTable();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not delete shop.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // SELECT SHOP
    // =========================

    private void selectShop() {

        int row = shopTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        selectedShopId =
                (int) tableModel.getValueAt(row, 0);

        String name =
                tableModel.getValueAt(row, 1).toString();

        String location =
                tableModel.getValueAt(row, 2).toString();

        nameField.setText(name);
        locationField.setText(location);
    }

    // =========================
    // REFRESH TABLE
    // =========================

    private void refreshTable() {

        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        for (Shop shop :
                shopService.getAllShops()) {

            tableModel.addRow(new Object[]{
                    shop.getId(),
                    shop.getName(),
                    shop.getLocation()
            });
        }
    }

    // =========================
    // CLEAR FORM
    // =========================

    private void clearForm() {

        selectedShopId = -1;

        nameField.setText("");
        locationField.setText("");

        if (shopTable != null) {
            shopTable.clearSelection();
        }

        nameField.requestFocus();
    }

    // =========================
    // LABEL
    // =========================

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        label.setForeground(TEXT);

        return label;
    }

    // =========================
    // BUTTON
    // =========================

    private JButton createButton(
            String text,
            Color color
    ) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        button.setForeground(Color.WHITE);
        button.setBackground(color);

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
                new EmptyBorder(9, 18, 9, 18)
        );

        return button;
    }
}
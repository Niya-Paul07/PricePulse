
package com.pricepulse.ui;

import com.pricepulse.Shop;
import com.pricepulse.ShopService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ShopPanel extends JPanel {

    private final ShopService shopService;

    private JTextField nameField;
    private JTextField locationField;

    private JTable shopTable;
    private DefaultTableModel tableModel;

    private int selectedShopId = -1;

    private static final Color GREEN = new Color(32, 112, 81);
    private static final Color DARK_GREEN = new Color(25, 67, 52);
    private static final Color BACKGROUND = new Color(247, 250, 246);
    private static final Color CREAM = new Color(250, 246, 230);
    private static final Color TEXT = new Color(38, 59, 48);
    private static final Color MUTED = new Color(108, 128, 115);
    private static final Color BORDER = new Color(225, 235, 226);
    private static final Color WHITE = Color.WHITE;

    public ShopPanel(ShopService shopService) {
        this.shopService = shopService;

        setLayout(new BorderLayout(0, 12));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        refreshTable();
    }

    // HEADER
    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Shop Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(DARK_GREEN);

        JLabel subtitle = new JLabel(
                "Register and manage shops for price comparison.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);

        return header;
    }

    // MAIN CONTENT
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);

        // Form and buttons stay together at the top.
        content.add(buildForm(), BorderLayout.NORTH);
        content.add(buildTable(), BorderLayout.CENTER);

        return content;
    }

    // SHOP FORM
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 12, 10, 12)));

        nameField = new JTextField();
        locationField = new JTextField();

        nameField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        locationField.setFont(new Font("SansSerif", Font.PLAIN, 13));

        styleTextField(nameField);
        styleTextField(locationField);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Shop name
        addLabel(form, gbc, 0, 0, "Shop Name");
        addInput(form, gbc, 0, 1, nameField);

        // Location
        addLabel(form, gbc, 1, 0, "Location");
        addInput(form, gbc, 1, 1, locationField);

        // ACTION BUTTONS — always directly below fields
        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 3));
        buttons.setOpaque(false);

        JButton addButton = createButton("Register Shop");
        JButton updateButton = createButton("Update");
        JButton deleteButton = createButton("Delete");
        JButton clearButton = createButton("Clear");

        addButton.addActionListener(e -> addShop());
        updateButton.addActionListener(e -> updateShop());
        deleteButton.addActionListener(e -> deleteShop());
        clearButton.addActionListener(e -> clearForm());

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 4, 0, 4);

        form.add(buttons, gbc);

        return form;
    }

    private void addLabel(
            JPanel panel,
            GridBagConstraints gbc,
            int column,
            int row,
            String text) {

        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;

        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT);

        panel.add(label, gbc);
    }

    private void addInput(
            JPanel panel,
            GridBagConstraints gbc,
            int column,
            int row,
            JTextField field) {

        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 1;

        panel.add(field, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(7, 8, 7, 8)));
    }

    // REGISTERED SHOPS TABLE
    private JPanel buildTable() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel title = new JLabel("Registered Shops");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Shop Name", "Location"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        shopTable = new JTable(tableModel);
        shopTable.setRowHeight(28);
        shopTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        shopTable.setSelectionBackground(new Color(222, 241, 226));
        shopTable.setSelectionForeground(TEXT);
        shopTable.setGridColor(BORDER);
        shopTable.setShowVerticalLines(false);

        shopTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        shopTable.getTableHeader().setBackground(CREAM);
        shopTable.getTableHeader().setForeground(DARK_GREEN);
        shopTable.getTableHeader().setReorderingAllowed(false);

        shopTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedShop();
            }
        });

        JScrollPane scrollPane = new JScrollPane(shopTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(WHITE);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // CREATE
    private void addShop() {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();

        if (name.isEmpty() || location.isEmpty()) {
            showMessage("Enter both the shop name and location.");
            return;
        }

        Shop shop = new Shop(name, location);

        if (shopService.addShop(shop)) {
            refreshTable();
            clearForm();
            showMessage("Shop registered successfully!");
        } else {
            showMessage(
                    "Shop could not be registered. "
                    + "Check the details and avoid duplicate names.");
        }
    }

    // UPDATE
    private void updateShop() {
        if (selectedShopId == -1) {
            showMessage("Select a shop from the table first.");
            return;
        }

        String name = nameField.getText().trim();
        String location = locationField.getText().trim();

        if (name.isEmpty() || location.isEmpty()) {
            showMessage("Enter both the shop name and location.");
            return;
        }

        Shop updatedShop = new Shop(
                selectedShopId, name, location);

        if (shopService.updateShop(updatedShop)) {
            refreshTable();
            clearForm();
            showMessage("Shop updated successfully!");
        } else {
            showMessage(
                    "Update failed. Check the details and "
                    + "make sure the shop name is unique.");
        }
    }

    // DELETE
    private void deleteShop() {
        if (selectedShopId == -1) {
            showMessage("Select a shop from the table first.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected shop?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        if (shopService.deleteShop(selectedShopId)) {
            refreshTable();
            clearForm();
            showMessage("Shop deleted successfully!");
        } else {
            showMessage("Could not delete the shop.");
        }
    }

    // LOAD SELECTED SHOP INTO THE FORM
    private void loadSelectedShop() {
        int row = shopTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        selectedShopId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        nameField.setText(
                tableModel.getValueAt(row, 1).toString());

        locationField.setText(
                tableModel.getValueAt(row, 2).toString());
    }

    // REFRESH TABLE
    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Shop shop : shopService.getAllShops()) {
            tableModel.addRow(new Object[]{
                    shop.getId(),
                    shop.getName(),
                    shop.getLocation()
            });
        }
    }

    // CLEAR FORM
    private void clearForm() {
        nameField.setText("");
        locationField.setText("");
        selectedShopId = -1;
        shopTable.clearSelection();
    }

    // BUTTON STYLE
    private JButton createButton(String text) {
        JButton button = new JButton(text);

        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(GREEN);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.setCursor(Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR));

        return button;
    }

    // MESSAGE DIALOG
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "PricePulse",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

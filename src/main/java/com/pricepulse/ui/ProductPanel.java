
package com.pricepulse.ui;

import com.pricepulse.Item;
import com.pricepulse.ItemService;
import com.pricepulse.Shop;
import com.pricepulse.ShopService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class ProductPanel extends JPanel {

    private final ItemService itemService;
    private final ShopService shopService;

    private JComboBox<String> nameCombo;
    private JTextField priceField;

    private JComboBox<String> categoryCombo;
    private JComboBox<String> unitCombo;
    private JComboBox<String> shopCombo;

    private JTable productTable;
    private DefaultTableModel tableModel;

    private int selectedItemId = -1;

    private static final Color GREEN = new Color(32, 112, 81);
    private static final Color DARK_GREEN = new Color(25, 67, 52);
    private static final Color BACKGROUND = new Color(247, 250, 246);
    private static final Color CREAM = new Color(250, 246, 230);
    private static final Color TEXT = new Color(38, 59, 48);
    private static final Color MUTED = new Color(108, 128, 115);
    private static final Color BORDER = new Color(225, 235, 226);
    private static final Color WHITE = Color.WHITE;

    public ProductPanel(ItemService itemService, ShopService shopService) {
        this.itemService = itemService;
        this.shopService = shopService;

        setLayout(new BorderLayout(0, 18));
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(25, 30, 25, 30));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        refreshShops();
        refreshProductNames();
        refreshTable();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Product Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(DARK_GREEN);

        JLabel subtitle = new JLabel(
                "Add products and track their prices at registered shops.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(6));
        header.add(subtitle);

        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.add(buildForm(), BorderLayout.NORTH);
        content.add(buildTable(), BorderLayout.CENTER);
        return content;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(15, 15, 15, 15)));

        // Editable dropdown: select an existing product or type a new one.
        nameCombo = new JComboBox<>();
        nameCombo.setEditable(true);
        nameCombo.setMaximumRowCount(8);

        priceField = new JTextField(12);

        categoryCombo = new JComboBox<>(new String[]{
                "Groceries", "Fruits & Vegetables", "Dairy",
                "Beverages", "Personal Care", "Household",
                "Electronics", "Other"
        });

        unitCombo = new JComboBox<>(new String[]{
                "Piece", "Kg", "Gram", "Litre", "Ml",
                "Packet", "Bottle", "Dozen", "Box", "Pack"
        });

        shopCombo = new JComboBox<>();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 7, 6, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addField(form, gbc, 0, 0, "Product Name", nameCombo);
        addField(form, gbc, 1, 0, "Category", categoryCombo);
        addField(form, gbc, 0, 2, "Unit", unitCombo);
        addField(form, gbc, 1, 2, "Registered Shop", shopCombo);
        addField(form, gbc, 0, 4, "Price (₹)", priceField);

        JButton addButton = createButton("Add Product");
        JButton updateButton = createButton("Update");
        JButton deleteButton = createButton("Delete");
        JButton clearButton = createButton("Clear");
        JButton refreshShopsButton = createButton("Refresh Shops");

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearForm());
        refreshShopsButton.addActionListener(e -> {
            refreshShops();
            refreshProductNames();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 4));
        buttons.setOpaque(false);
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);
        buttons.add(refreshShopsButton);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        form.add(buttons, gbc);

        return form;
    }

    private void addField(JPanel panel, GridBagConstraints gbc,
                          int column, int labelRow,
                          String labelText, JComponent field) {
        gbc.gridx = column;
        gbc.gridy = labelRow;
        gbc.gridwidth = 1;
        gbc.weightx = 0;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT);
        panel.add(label, gbc);

        gbc.gridy = labelRow + 1;
        gbc.weightx = 1;

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));

        if (field instanceof JTextField) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER),
                    new EmptyBorder(7, 8, 7, 8)));
        }

        panel.add(field, gbc);
    }

    private JPanel buildTable() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel title = new JLabel("Products");
        title.setFont(new Font("SansSerif", Font.BOLD, 17));
        title.setForeground(TEXT);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Product", "Category",
                    "Unit", "Shop", "Price (₹)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setRowHeight(29);
        productTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        productTable.setSelectionBackground(new Color(222, 241, 226));
        productTable.setSelectionForeground(TEXT);
        productTable.setGridColor(BORDER);
        productTable.setShowVerticalLines(false);
        productTable.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12));
        productTable.getTableHeader().setBackground(CREAM);
        productTable.getTableHeader().setForeground(DARK_GREEN);
        productTable.getTableHeader().setReorderingAllowed(false);

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedProduct();
            }
        });

        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(WHITE);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void addProduct() {
        if (!validateForm()) {
            return;
        }

        String name = getProductName();
        String category = (String) categoryCombo.getSelectedItem();
        String unit = (String) unitCombo.getSelectedItem();
        String shop = (String) shopCombo.getSelectedItem();
        double price = readPrice();

        // Reuse the existing spelling when a product already exists.
        name = findExistingProductName(name);

        Item item = new Item(name, category, unit, price, shop);

        if (itemService.addItem(item)) {
            refreshTable();
            refreshProductNames();
            clearForm();
            showMessage("Product price added successfully!");
        } else {
            showMessage(
                    "Could not add product. This product may already "
                    + "have a price entry for this shop.");
        }
    }

    private void updateProduct() {
        if (selectedItemId == -1) {
            showMessage("Please select a product from the table.");
            return;
        }

        if (!validateForm()) {
            return;
        }

        String name = findExistingProductName(getProductName());

        Item updatedItem = new Item(
                selectedItemId,
                name,
                (String) categoryCombo.getSelectedItem(),
                (String) unitCombo.getSelectedItem(),
                readPrice(),
                (String) shopCombo.getSelectedItem());

        if (itemService.updateItem(updatedItem)) {
            refreshTable();
            refreshProductNames();
            clearForm();
            showMessage("Product updated successfully!");
        } else {
            showMessage("Could not update product. Check the details.");
        }
    }

    private void deleteProduct() {
        if (selectedItemId == -1) {
            showMessage("Please select a product to delete.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        if (itemService.deleteItem(selectedItemId)) {
            refreshTable();
            refreshProductNames();
            clearForm();
            showMessage("Product deleted successfully!");
        } else {
            showMessage("Could not delete product.");
        }
    }

    private boolean validateForm() {
        String name = getProductName();

        if (name.isEmpty()) {
            showMessage("Please select or enter a product name.");
            nameCombo.requestFocusInWindow();
            return false;
        }

        if (shopCombo.getSelectedItem() == null
                || shopCombo.getSelectedItem().toString().isEmpty()) {
            showMessage("Please register a shop first, then select it.");
            return false;
        }

        try {
            double price = readPrice();

            if (!Double.isFinite(price) || price < 0) {
                showMessage("Price must be a valid non-negative number.");
                return false;
            }
        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid price.");
            priceField.requestFocusInWindow();
            return false;
        }

        return true;
    }

    private String getProductName() {
        Object selected = nameCombo.getEditor().getItem();
        return selected == null ? "" : selected.toString().trim();
    }

    private String findExistingProductName(String enteredName) {
        for (Item item : itemService.getAllItems()) {
            if (item.getName().trim().equalsIgnoreCase(enteredName.trim())) {
                return item.getName();
            }
        }
        return enteredName.trim();
    }

    private void refreshProductNames() {
        String currentName = getProductName();

        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Item item : itemService.getAllItems()) {
            if (item.getName() != null && !item.getName().trim().isEmpty()) {
                names.add(item.getName().trim());
            }
        }

        nameCombo.removeAllItems();
        for (String name : names) {
            nameCombo.addItem(name);
        }

        nameCombo.setSelectedItem(currentName);
        nameCombo.setEditable(true);
    }

    private double readPrice() {
        return Double.parseDouble(priceField.getText().trim());
    }

    private void loadSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row == -1) {
            return;
        }

        selectedItemId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString());

        nameCombo.setSelectedItem(tableModel.getValueAt(row, 1).toString());
        categoryCombo.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        unitCombo.setSelectedItem(tableModel.getValueAt(row, 3).toString());

        selectShop(tableModel.getValueAt(row, 4).toString());
        priceField.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void selectShop(String shopName) {
        for (int i = 0; i < shopCombo.getItemCount(); i++) {
            if (shopCombo.getItemAt(i).equals(shopName)) {
                shopCombo.setSelectedIndex(i);
                return;
            }
        }

        shopCombo.addItem(shopName);
        shopCombo.setSelectedItem(shopName);
    }

    private void refreshShops() {
        String previousSelection = (String) shopCombo.getSelectedItem();
        shopCombo.removeAllItems();

        for (Shop shop : shopService.getAllShops()) {
            shopCombo.addItem(shop.getName());
        }

        if (previousSelection != null) {
            shopCombo.setSelectedItem(previousSelection);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Item item : itemService.getAllItems()) {
            tableModel.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getCategory(),
                    item.getUnit(),
                    item.getShopName(),
                    item.getPrice()
            });
        }
    }

    private void clearForm() {
        nameCombo.setSelectedItem("");
        priceField.setText("");
        selectedItemId = -1;

        if (productTable != null) {
            productTable.clearSelection();
        }

        if (shopCombo.getItemCount() > 0) {
            shopCombo.setSelectedIndex(0);
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setForeground(WHITE);
        button.setBackground(GREEN);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(8, 11, 8, 11));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(
                this, message, "PricePulse",
                JOptionPane.INFORMATION_MESSAGE);
    }
}

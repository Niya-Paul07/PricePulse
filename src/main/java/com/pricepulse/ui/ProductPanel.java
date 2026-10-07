package com.pricepulse.ui;

import com.pricepulse.Item;
import com.pricepulse.ItemService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductPanel extends JPanel {

    private static final Color PRIMARY = new Color(37, 99, 235);
    private static final Color BACKGROUND = new Color(248, 250, 252);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);

    private final ItemService itemService;

    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JTextField unitField;

    private JTable productTable;
    private DefaultTableModel tableModel;

    private int selectedItemId = -1;

    private final String[] categories = {
            "Grocery",
            "Fruits",
            "Vegetables",
            "Dairy",
            "Beverages",
            "Snacks",
            "Personal Care",
            "Household"
    };

    public ProductPanel(ItemService itemService) {

        this.itemService = itemService;

        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(new EmptyBorder(25, 35, 30, 35));

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        refreshTable();
    }

    // ---------------------------------------------------------
    // HEADER
    // ---------------------------------------------------------

    private JPanel createHeader() {

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(BACKGROUND);

        JLabel title = new JLabel("Products");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(TEXT);

        JLabel description = new JLabel(
                "Add, edit and manage the products used in PricePulse."
        );
        description.setFont(new Font("Arial", Font.PLAIN, 14));
        description.setForeground(MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(description);
        header.add(Box.createVerticalStrut(20));

        return header;
    }

    // ---------------------------------------------------------
    // MAIN CONTENT
    // ---------------------------------------------------------

    private JPanel createMainContent() {

        JPanel main = new JPanel();
        main.setLayout(new BorderLayout(0, 20));
        main.setBackground(BACKGROUND);

        main.add(createFormPanel(), BorderLayout.NORTH);
        main.add(createTablePanel(), BorderLayout.CENTER);

        return main;
    }

    // ---------------------------------------------------------
    // FORM
    // ---------------------------------------------------------

    private JPanel createFormPanel() {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(CARD);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = createLabel("Product Name");
        JLabel categoryLabel = createLabel("Category");
        JLabel unitLabel = createLabel("Unit");

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(180, 35));

        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setPreferredSize(new Dimension(180, 35));

        unitField = new JTextField();
        unitField.setPreferredSize(new Dimension(180, 35));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        fields.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        fields.add(nameField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        fields.add(categoryLabel, gbc);

        gbc.gridx = 3;
        gbc.weightx = 1;
        fields.add(categoryCombo, gbc);

        gbc.gridx = 4;
        gbc.weightx = 0;
        fields.add(unitLabel, gbc);

        gbc.gridx = 5;
        gbc.weightx = 1;
        fields.add(unitField, gbc);

        card.add(fields, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttons.setBackground(CARD);

        JButton addButton = createButton("Add Product", PRIMARY);
        JButton updateButton = createButton("Update", new Color(16, 185, 129));
        JButton deleteButton = createButton("Delete", new Color(220, 38, 38));
        JButton clearButton = createButton("Clear", MUTED);

        addButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearForm());

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        card.add(buttons, BorderLayout.SOUTH);

        return card;
    }

    // ---------------------------------------------------------
    // TABLE
    // ---------------------------------------------------------

    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel tableTitle = new JLabel("Product List");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 17));
        tableTitle.setForeground(TEXT);

        tableTitle.setBorder(new EmptyBorder(0, 0, 12, 0));

        panel.add(tableTitle, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Product Name", "Category", "Unit"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);

        productTable.setRowHeight(30);
        productTable.setFont(new Font("Arial", Font.PLAIN, 13));
        productTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        productTable.getTableHeader().setBackground(
                new Color(241, 245, 249)
        );

        productTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        productTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(50);

        productTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(200);

        productTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(150);

        productTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(120);

        productTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                selectProduct();
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setBorder(null);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ---------------------------------------------------------
    // ADD PRODUCT
    // ---------------------------------------------------------

    private void addProduct() {

        String name = nameField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String unit = unitField.getText().trim();

        if (name.isEmpty() || unit.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Product name and unit are required.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Item item = new Item(name, category, unit);

        boolean success = itemService.addItem(item);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Product added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            refreshTable();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not add product.\n" +
                    "The product name may already exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ---------------------------------------------------------
    // UPDATE PRODUCT
    // ---------------------------------------------------------

    private void updateProduct() {

        if (selectedItemId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a product from the table first.",
                    "Update Product",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String name = nameField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String unit = unitField.getText().trim();

        if (name.isEmpty() || unit.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Product name and unit are required.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Item updatedItem =
                new Item(selectedItemId, name, category, unit);

        boolean success = itemService.updateItem(updatedItem);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Product updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            refreshTable();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not update product.\n" +
                    "The product name may already exist.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ---------------------------------------------------------
    // DELETE PRODUCT
    // ---------------------------------------------------------

    private void deleteProduct() {

        if (selectedItemId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a product from the table first.",
                    "Delete Product",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success =
                itemService.deleteItem(selectedItemId);

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Product deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            refreshTable();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Could not delete product.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ---------------------------------------------------------
    // SELECT PRODUCT FROM TABLE
    // ---------------------------------------------------------

    private void selectProduct() {

        int row = productTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        selectedItemId =
                (int) tableModel.getValueAt(row, 0);

        nameField.setText(
                tableModel.getValueAt(row, 1).toString()
        );

        categoryCombo.setSelectedItem(
                tableModel.getValueAt(row, 2).toString()
        );

        unitField.setText(
                tableModel.getValueAt(row, 3).toString()
        );
    }

    // ---------------------------------------------------------
    // REFRESH TABLE
    // ---------------------------------------------------------

    private void refreshTable() {

        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        List<Item> items =
                itemService.getAllItems();

        for (Item item : items) {

            tableModel.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    item.getCategory(),
                    item.getUnit()
            });
        }
    }

    // ---------------------------------------------------------
    // CLEAR FORM
    // ---------------------------------------------------------

    private void clearForm() {

        selectedItemId = -1;

        nameField.setText("");
        categoryCombo.setSelectedIndex(0);
        unitField.setText("");

        productTable.clearSelection();
    }

    // ---------------------------------------------------------
    // UI HELPERS
    // ---------------------------------------------------------

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(TEXT);

        return label;
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }
}

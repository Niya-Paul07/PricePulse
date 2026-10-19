package com.pricepulse.ui;

import com.pricepulse.ItemService;
import com.pricepulse.ShopService;
import com.pricepulse.auth.AuthService;
import com.pricepulse.crud.PriceEntryService;
import com.pricepulse.model.PriceEntry;
import com.pricepulse.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PriceEntryPanel extends JPanel {

    private final PriceEntryService priceEntryService;
    private final User currentUser;

    private final JComboBox<com.pricepulse.model.Item> itemCombo =
            new JComboBox<>();

    private final JComboBox<com.pricepulse.model.Shop> shopCombo =
            new JComboBox<>();

    private final JTextField priceField = new JTextField(10);
    private final JTextField dateField =
            new JTextField(LocalDate.now().toString(), 10);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(
                    new String[]{
                            "ID", "Product", "Shop",
                            "User", "Price", "Date"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(tableModel);

    public PriceEntryPanel(
            ItemService itemService,
            ShopService shopService,
            AuthService authService,
            User currentUser) {

        this.currentUser = currentUser;

        List<com.pricepulse.model.Item> modelItems =
                new ArrayList<>();

        for (com.pricepulse.Item item : itemService.getAllItems()) {
            modelItems.add(new com.pricepulse.model.Item(
                    item.getId(),
                    item.getName(),
                    item.getCategory(),
                    item.getUnit()
            ));
        }

        List<com.pricepulse.model.Shop> modelShops =
                new ArrayList<>();

        for (com.pricepulse.Shop shop : shopService.getAllShops()) {
            modelShops.add(new com.pricepulse.model.Shop(
                    shop.getId(),
                    shop.getName(),
                    shop.getLocation()
            ));
        }

        List<User> modelUsers =
                new ArrayList<>(authService.getAllUsers());

        if (currentUser != null &&
                modelUsers.stream().noneMatch(
                        u -> u.getId() == currentUser.getId())) {
            modelUsers.add(currentUser);
        }
System.out.println("Products received: " + modelItems.size());
System.out.println("Shops received: " + modelShops.size());

        priceEntryService = new PriceEntryService(
                modelItems, modelShops, modelUsers
        );
// Load products into the product dropdown
for (com.pricepulse.model.Item item : modelItems) {
    itemCombo.addItem(item);
}

// Load shops into the shop dropdown
for (com.pricepulse.model.Shop shop : modelShops) {
    shopCombo.addItem(shop);
}

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Price Entry Management");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(15, 0, 15, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(form, gbc, 0, "Product:", itemCombo);
        addField(form, gbc, 1, "Shop:", shopCombo);
        addField(form, gbc, 2, "Price:", priceField);
        addField(form, gbc, 3, "Date (yyyy-MM-dd):", dateField);

        JButton submitButton = new JButton("Submit Price");
        JButton updateButton = new JButton("Update Selected");
        JButton deleteButton = new JButton("Delete Selected");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setBackground(Color.WHITE);
        buttons.add(submitButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(form, BorderLayout.CENTER);
        topPanel.add(buttons, BorderLayout.SOUTH);

        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> submitPrice());
        updateButton.addActionListener(e -> updatePrice());
        deleteButton.addActionListener(e -> deletePrice());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        refreshTable();
    }

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component field) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private double readPrice() {
        double price = Double.parseDouble(priceField.getText().trim());

        if (!Double.isFinite(price) || price <= 0) {
            throw new IllegalArgumentException(
                    "Price must be a positive number."
            );
        }

        return price;
    }

    private LocalDate readDate() {
        return LocalDate.parse(dateField.getText().trim());
    }

    private void submitPrice() {
        try {
            if (currentUser == null) {
                throw new IllegalStateException(
                        "No logged-in user was provided."
                );
            }

            com.pricepulse.model.Item item =
                    (com.pricepulse.model.Item) itemCombo.getSelectedItem();

            com.pricepulse.model.Shop shop =
                    (com.pricepulse.model.Shop) shopCombo.getSelectedItem();

            if (item == null || shop == null) {
                throw new IllegalStateException(
                        "Please add a product and a shop first."
                );
            }

            priceEntryService.submitPriceEntry(
                    item.getId(),
                    shop.getId(),
                    currentUser.getId(),
                    readPrice(),
                    readDate()
            );

            refreshTable();
            clearForm();

            JOptionPane.showMessageDialog(
                    this, "Price entry saved successfully!"
            );

        } catch (NumberFormatException ex) {
            showError("Enter a valid price.");
        } catch (java.time.format.DateTimeParseException ex) {
            showError("Enter the date as yyyy-MM-dd.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void updatePrice() {
        int id = getSelectedEntryId();
        if (id == -1) {
            showError("Select a price entry from the table first.");
            return;
        }

        try {
            boolean updated = priceEntryService.updatePriceEntry(
                    id, readPrice(), readDate()
            );

            if (!updated) {
                showError("The selected entry could not be updated.");
                return;
            }

            refreshTable();
            clearForm();

            JOptionPane.showMessageDialog(
                    this, "Price entry updated successfully!"
            );

        } catch (NumberFormatException ex) {
            showError("Enter a valid price.");
        } catch (java.time.format.DateTimeParseException ex) {
            showError("Enter the date as yyyy-MM-dd.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void deletePrice() {
        int id = getSelectedEntryId();
        if (id == -1) {
            showError("Select a price entry from the table first.");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected price entry?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (priceEntryService.deletePriceEntry(id)) {
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(
                        this, "Price entry deleted."
                );
            } else {
                showError("The selected entry could not be deleted.");
            }
        } catch (IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private int getSelectedEntryId() {
        int row = table.getSelectedRow();

        if (row == -1) {
            return -1;
        }

        int modelRow = table.convertRowIndexToModel(row);

        return Integer.parseInt(
                tableModel.getValueAt(modelRow, 0).toString()
        );
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();

        if (row == -1) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(row);

        priceField.setText(
                tableModel.getValueAt(modelRow, 4).toString()
        );

        dateField.setText(
                tableModel.getValueAt(modelRow, 5).toString()
        );
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        for (PriceEntry entry : priceEntryService.getAllEntries()) {
            tableModel.addRow(new Object[]{
                    entry.getId(),
                    entry.getItem().getName(),
                    entry.getShop().getName(),
                    entry.getUser().getName(),
                    entry.getPrice(),
                    entry.getDate()
            });
        }
    }

    private void clearForm() {
        priceField.setText("");
        dateField.setText(LocalDate.now().toString());
        table.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message == null ? "An unexpected error occurred." : message,
                "Price Entry Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
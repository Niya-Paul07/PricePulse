package com.pricepulse.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.pricepulse.model.PriceEntry;

public class CheapestShopPanel extends JPanel {

    private JTextField productField;
    private JButton searchButton;
    private JLabel resultLabel;

    private List<PriceEntry> priceEntries;

    public CheapestShopPanel(List<PriceEntry> priceEntries) {

        this.priceEntries = priceEntries;

        setLayout(new FlowLayout());

        JLabel title = new JLabel("Cheapest Shop Lookup");

        productField = new JTextField(15);

        searchButton = new JButton("Find Cheapest Shop");

        resultLabel = new JLabel(
                "Enter a product to find the cheapest shop."
        );

        searchButton.addActionListener(e -> findCheapestShop());

        add(title);
        add(new JLabel("Product:"));
        add(productField);
        add(searchButton);
        add(resultLabel);
    }

    private void findCheapestShop() {

        String product = productField.getText().trim();

        if (product.isEmpty()) {
            resultLabel.setText("Please enter a product name.");
            return;
        }

        PriceEntry cheapest = null;

        for (PriceEntry entry : priceEntries) {

            if (entry.getItem().getName().equalsIgnoreCase(product)) {

                if (cheapest == null ||
                        entry.getPrice() < cheapest.getPrice()) {

                    cheapest = entry;
                }
            }
        }

        if (cheapest == null) {

            resultLabel.setText(
                    "No price found for " + product
            );

        } else {

            resultLabel.setText(
                    "Cheapest: " +
                    cheapest.getShop().getName() +
                    " - Rs. " +
                    cheapest.getPrice()
            );
        }
    }
}
package com.pricepulse.ui;

import javax.swing.*;

public class CheapestShopPanel extends JPanel {
    JTextField productField;
    JButton searchButton;
    JLabel resultLabel;
    public CheapestShopPanel() {

        JLabel title = new JLabel("Cheapest Shop Lookup");

        add(title);
        productField = new JTextField(15);
searchButton = new JButton("Find Cheapest Shop");
searchButton.addActionListener(e -> {
    String product = productField.getText();

    if (product.isEmpty()) {
        resultLabel.setText("Please enter a product name.");
    } else {
        resultLabel.setText("Searching for: " + product);
    }
});
add(new JLabel("Product:"));
add(productField);
add(searchButton);
resultLabel = new JLabel("Enter a product to find the cheapest shop.");
add(resultLabel);
    }
}
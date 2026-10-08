package com.pricepulse.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import com.pricepulse.model.PriceEntry;
import com.pricepulse.service.CheapestShopService;
public class CheapestShopPanel extends JPanel {

    private JTextField productField;
    private JButton searchButton;
    private JLabel resultLabel;

    private List<PriceEntry> priceEntries;
    private CheapestShopService
    CheapestShopService;

    public CheapestShopPanel(List<PriceEntry> priceEntries) {

        this.priceEntries = priceEntries;
        this.CheapestShopService=new CheapestShopService();

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

    PriceEntry cheapest =
            CheapestShopService.findCheapestShop(
                    product,
                    priceEntries
            );

    if (cheapest == null) {
        resultLabel.setText("No price found for " + product);
    } else {
        resultLabel.setText(
                "Cheapest: " +
                cheapest.getShop().getName() +
                " - Rs. " +
                cheapest.getPrice()
        );
    }
}}
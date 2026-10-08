package com.pricepulse.service;

import java.util.List;

import com.pricepulse.model.PriceEntry;

public class CheapestShopService {

    public PriceEntry findCheapestShop(
            String productName,
            List<PriceEntry> priceEntries) {

        PriceEntry cheapest = null;

        for (PriceEntry entry : priceEntries) {

            if (entry.getItem().getName()
                    .equalsIgnoreCase(productName)) {

                if (cheapest == null ||
                    entry.getPrice() < cheapest.getPrice()) {

                    cheapest = entry;
                }
            }
        }

        return cheapest;
    }
}
package com.pricepulse.analysis;

import com.pricepulse.model.Item;
import com.pricepulse.model.Shop;
import com.pricepulse.model.PriceEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Detects significant changes in product prices.
 */
public class SpikeDetector extends PriceAnalyzer {

    public SpikeDetector(List<PriceEntry> data) {
        super(data);
    }

    @Override
    public void analyze() {
        System.out.println("Price spike detection completed.");
    }

    /**
     * Checks whether the latest price change exceeds the threshold.
     *
     * @param item item to analyze
     * @param shop shop to analyze
     * @param threshold percentage threshold
     * @return true if the absolute percentage change is greater than threshold
     */
    public boolean detectSpike(
            Item item,
            Shop shop,
            double threshold) {

        List<PriceEntry> history = getHistory(item, shop);

        if (history.size() < 2) {
            return false;
        }

        history.sort(
                Comparator.comparing(PriceEntry::getDate)
        );

        PriceEntry previous =
                history.get(history.size() - 2);

        PriceEntry current =
                history.get(history.size() - 1);

        double oldPrice = previous.getPrice();
        double newPrice = current.getPrice();

        if (oldPrice == 0) {
            return false;
        }

        double percentageChange =
                ((newPrice - oldPrice) / oldPrice) * 100;

        return Math.abs(percentageChange) > threshold;
    }

    /**
     * Returns the latest percentage price change.
     *
     * Positive value = price increased.
     * Negative value = price decreased.
     */
    public double getPercentageChange(
            Item item,
            Shop shop) {

        List<PriceEntry> history = getHistory(item, shop);

        if (history.size() < 2) {
            return 0.0;
        }

        history.sort(
                Comparator.comparing(PriceEntry::getDate)
        );

        PriceEntry previous =
                history.get(history.size() - 2);

        PriceEntry current =
                history.get(history.size() - 1);

        double oldPrice = previous.getPrice();
        double newPrice = current.getPrice();

        if (oldPrice == 0) {
            return 0.0;
        }

        return ((newPrice - oldPrice) / oldPrice) * 100;
    }

    /**
     * Gets all price entries for an item at a shop.
     */
    private List<PriceEntry> getHistory(
            Item item,
            Shop shop) {

        List<PriceEntry> history = new ArrayList<>();

        if (item == null || shop == null) {
            return history;
        }

        for (PriceEntry entry : data) {

            if (entry.getItem() != null
                    && entry.getShop() != null
                    && entry.getItem().getId() == item.getId()
                    && entry.getShop().getId() == shop.getId()) {

                history.add(entry);
            }
        }

        return history;
    }
}

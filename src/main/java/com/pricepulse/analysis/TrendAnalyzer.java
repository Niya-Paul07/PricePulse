package com.pricepulse.analysis;

import com.pricepulse.model.Item;
import com.pricepulse.model.Shop;
import com.pricepulse.model.PriceEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Analyzes price history and determines the price trend.
 */
public class TrendAnalyzer extends PriceAnalyzer {

    private static final double STABLE_TOLERANCE = 0.01;

    public TrendAnalyzer(List<PriceEntry> data) {
        super(data);
    }

    @Override
    public void analyze() {
        System.out.println("Trend analysis completed.");
    }

    /**
     * Determines the trend of an item at a particular shop.
     *
     * @param item item to analyze
     * @param shop shop to analyze
     * @return Rising, Falling, Stable, or Not enough historical data
     */
    public String getTrend(Item item, Shop shop) {

        List<PriceEntry> history = getHistory(item, shop);

        if (history.size() < 2) {
            return "Not enough historical data";
        }

        // Sort entries from oldest to newest
        history.sort(
                Comparator.comparing(PriceEntry::getDate)
        );

        double firstPrice = history.get(0).getPrice();
        double lastPrice = history.get(history.size() - 1).getPrice();

        double difference = lastPrice - firstPrice;

        if (difference > STABLE_TOLERANCE) {
            return "Rising";
        }

        if (difference < -STABLE_TOLERANCE) {
            return "Falling";
        }

        return "Stable";
    }

    /**
     * Gets all price entries for a particular item at a particular shop.
     */
    public List<PriceEntry> getHistory(Item item, Shop shop) {

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

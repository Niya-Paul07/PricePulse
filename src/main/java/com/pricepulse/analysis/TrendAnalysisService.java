package com.pricepulse.analysis;

import com.pricepulse.model.Item;
import com.pricepulse.model.Shop;
import com.pricepulse.model.PriceEntry;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service that combines trend analysis and price-change detection.
 *
 * This class is intended to be used by PriceTrackerService
 * or another service/controller in the application.
 */
public class TrendAnalysisService {

    private final List<PriceEntry> entries;

    private final TrendAnalyzer trendAnalyzer;
    private final SpikeDetector spikeDetector;

    public TrendAnalysisService(List<PriceEntry> entries) {

        this.entries = entries;

        this.trendAnalyzer =
                new TrendAnalyzer(entries);

        this.spikeDetector =
                new SpikeDetector(entries);
    }

    /**
     * Returns the price history of an item at a shop,
     * sorted from oldest to newest.
     */
    public List<PriceEntry> getPriceHistory(
            Item item,
            Shop shop) {

        List<PriceEntry> history = new ArrayList<>();

        if (item == null || shop == null) {
            return history;
        }

        for (PriceEntry entry : entries) {

            if (entry.getItem() != null
                    && entry.getShop() != null
                    && entry.getItem().getId() == item.getId()
                    && entry.getShop().getId() == shop.getId()) {

                history.add(entry);
            }
        }

        history.sort(
                Comparator.comparing(PriceEntry::getDate)
        );

        return history;
    }

    /**
     * Returns the price trend.
     */
    public String getTrend(
            Item item,
            Shop shop) {

        return trendAnalyzer.getTrend(item, shop);
    }

    /**
     * Returns the latest percentage price change.
     */
    public double getPercentageChange(
            Item item,
            Shop shop) {

        return spikeDetector.getPercentageChange(item, shop);
    }

    /**
     * Checks whether the latest price change
     * exceeds the given percentage threshold.
     */
    public boolean detectSpike(
            Item item,
            Shop shop,
            double threshold) {

        return spikeDetector.detectSpike(
                item,
                shop,
                threshold
        );
    }

    /**
     * Returns the latest PriceEntry.
     */
    public PriceEntry getLatestEntry(
            Item item,
            Shop shop) {

        List<PriceEntry> history =
                getPriceHistory(item, shop);

        if (history.isEmpty()) {
            return null;
        }

        return history.get(history.size() - 1);
    }
}

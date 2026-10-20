package com.pricepulse.analysis;

import com.pricepulse.model.PriceEntry;

import java.util.List;

/**
 * Abstract base class for price analysis.
 */
public abstract class PriceAnalyzer {

    protected List<PriceEntry> data;

    public PriceAnalyzer(List<PriceEntry> data) {
        this.data = data;
    }

    /**
     * Performs the analysis.
     */
    public abstract void analyze();
}

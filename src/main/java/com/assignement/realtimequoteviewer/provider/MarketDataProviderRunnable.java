package com.assignement.realtimequoteviewer.provider;

import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;

import java.util.concurrent.BlockingQueue;

public class MarketDataProviderRunnable implements Runnable {

    private final MarketDataProvider marketDataProvider ;

    public MarketDataProviderRunnable() {
        this.marketDataProvider = new MarketDataProvider();
    }

    public MarketDataProviderRunnable(BlockingQueue<PriceUpdateEvent> priceUpdateChannel) {

        this.marketDataProvider = new MarketDataProvider(priceUpdateChannel);
    }

    public MarketDataProviderRunnable(MarketDataProvider marketDataProvider) {
        this.marketDataProvider = marketDataProvider;
    }

    @Override
    public void run() {
        this.marketDataProvider.runMarketDataProvider();
    }
}

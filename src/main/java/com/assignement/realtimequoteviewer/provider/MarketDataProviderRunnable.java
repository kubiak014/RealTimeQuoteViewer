package com.assignement.realtimequoteviewer.provider;

import java.util.concurrent.BlockingQueue;

public class MarketDataProviderRunnable implements Runnable {

    private final MarketDataProvider marketDataProvider ;

    public MarketDataProviderRunnable() {
        this.marketDataProvider = new MarketDataProvider();
    }

    public MarketDataProviderRunnable(BlockingQueue<Object> priceUpdateChannel) {

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

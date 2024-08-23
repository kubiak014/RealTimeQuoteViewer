package com.assignement.realtimequoteviewer.provider;

public class MarketDataProviderRunnable implements Runnable {

    private final MarketDataProvider marketDataProvider ;

    public MarketDataProviderRunnable() {
        this.marketDataProvider = new MarketDataProvider();
    }
    @Override
    public void run() {
        this.marketDataProvider.runMarketDataProvider();
    }
}

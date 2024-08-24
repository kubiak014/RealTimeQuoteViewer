package com.assignement.realtimequoteviewer.provider;

import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;

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

    public MarketDataProviderRunnable(BlockingQueue<PriceUpdateEvent> priceUpdateChannel, SecurityRepository securityRepository) {
        this.marketDataProvider = new MarketDataProvider(priceUpdateChannel, securityRepository);
    }

    @Override
    public void run() {
        this.marketDataProvider.runMarketDataProvider();
    }
}

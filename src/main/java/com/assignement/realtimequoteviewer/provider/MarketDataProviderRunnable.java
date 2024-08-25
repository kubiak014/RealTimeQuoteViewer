package com.assignement.realtimequoteviewer.provider;

import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;

import java.util.concurrent.BlockingQueue;

public class MarketDataProviderRunnable implements Runnable {

    private final MarketDataProvider marketDataProvider ;
    private final String producerName;

    public MarketDataProviderRunnable() {
        this.marketDataProvider = new MarketDataProvider();
        this.producerName = "DefaultProducer";
    }

    public MarketDataProviderRunnable(BlockingQueue<PriceUpdateEvent> priceUpdateChannel) {

        this.marketDataProvider = new MarketDataProvider(priceUpdateChannel);
        this.producerName = "DefaultProducer";
    }

    public MarketDataProviderRunnable(MarketDataProvider marketDataProvider) {
        this.marketDataProvider = marketDataProvider;
        this.producerName = "DefaultProducer";
    }

    public MarketDataProviderRunnable(BlockingQueue<PriceUpdateEvent> priceUpdateChannel, SecurityRepository securityRepository) {
        this.marketDataProvider = new MarketDataProvider(priceUpdateChannel, securityRepository);
        this.producerName = "DefaultProducer";
    }

    public MarketDataProviderRunnable(BlockingQueue<PriceUpdateEvent> priceUpdateChannel, SecurityRepository securityRepository, String producerName) {
        this.marketDataProvider = new MarketDataProvider(priceUpdateChannel, securityRepository);
        this.producerName = producerName;
    }

    @Override
    public void run() {
        System.out.println("Starting Market Data provider with name [ " + this.producerName + " ]");
        this.marketDataProvider.runMarketDataProvider();
    }
}

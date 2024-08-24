package com.assignement.realtimequoteviewer.provider;


import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MarketDataProvider {

    private final BlockingQueue<PriceUpdateEvent> priceUpdateChannel;

    @Autowired
    public MarketDataProvider() {
        this.priceUpdateChannel = new LinkedBlockingQueue<>();
    }

    public MarketDataProvider(BlockingQueue<PriceUpdateEvent> priceUpdateChannel) {
        this.priceUpdateChannel = priceUpdateChannel;
    }

    public void runMarketDataProvider() {
        //TODO: if update exist, print new portfolio valuation
        while (true) {

            if (marketTimer()) {;
                PriceUpdateEvent priceUpdateEvent = new PriceUpdateEvent();
                if (this.priceUpdateChannel.offer(priceUpdateEvent)) {
                    System.out.println("/!\\----------[Market Data Provider] Sending Market Data update: " + priceUpdateEvent + " ----------/!\\");
                } else {
                    System.out.println("/!\\/!\\ ---------- Price Channel full, unable to insert price update " + priceUpdateEvent + ". ----------/!\\/!\\");
                }
            }
        }
    }

    private boolean marketTimer() {
        Random random = new Random();
        double timer = random.nextDouble(0.5, 2) * 1000;
        try {
            Thread.sleep((long) (timer));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return random.nextBoolean();
    }
}

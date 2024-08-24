package com.assignement.realtimequoteviewer.provider;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MarketDataProvider {

    private final BlockingQueue<Object> priceUpdateChannel;

    @Autowired public MarketDataProvider (){
        this.priceUpdateChannel = new LinkedBlockingQueue<>();
    }

    public MarketDataProvider(BlockingQueue<Object> priceUpdateChannel) {
        this.priceUpdateChannel = priceUpdateChannel;
    }

    public void runMarketDataProvider(){
        //TODO: if update exist, print new portfolio valuation
        while(true) {

            if ( marketTimer()) {
                if(this.priceUpdateChannel.offer(new Object())) {
                    System.out.println("/!\\----------[Market Data Provider] Sending Market Data update ----------/!\\");
                } else {
                    System.out.println("/!\\/!\\ ---------- Price Channel full, unable to insert price update. ----------/!\\/!\\");
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

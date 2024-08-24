package com.assignement.realtimequoteviewer.provider;


import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import com.assignement.realtimequoteviewer.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MarketDataProvider {

    private BlockingQueue<PriceUpdateEvent> priceUpdateChannel;

    @Autowired
    private SecurityService securityService;

    public MarketDataProvider(BlockingQueue<PriceUpdateEvent> priceUpdateChannel, SecurityService securityService) {
        this.priceUpdateChannel = priceUpdateChannel;
        this.securityService = securityService;
    }

    public MarketDataProvider() {
        this.priceUpdateChannel = new LinkedBlockingQueue<>();
    }

    public MarketDataProvider(BlockingQueue<PriceUpdateEvent> priceUpdateChannel) {
        this.priceUpdateChannel = priceUpdateChannel;
    }

    public MarketDataProvider(BlockingQueue<PriceUpdateEvent> priceUpdateChannel, SecurityRepository securityRepository) {
        this.priceUpdateChannel = priceUpdateChannel;
        this.securityService = new SecurityService(securityRepository);
    }

    public void runMarketDataProvider() {
        //TODO: if update exist, print new portfolio valuation
        while (true) {

            if (marketTimer()) {

                PriceUpdateEvent priceUpdateEvent = createPriceUpdateEvent();
                if (this.priceUpdateChannel.offer(priceUpdateEvent)) {
                    System.out.println("/!\\----------[Market Data Provider] Sending Market Data update: " + priceUpdateEvent + " ----------/!\\");
                } else {
                    System.out.println("/!\\/!\\ ---------- Price Channel full, unable to insert price update " + priceUpdateEvent + ". ----------/!\\/!\\");
                }
            }
        }
    }

    private PriceUpdateEvent createPriceUpdateEvent() {
        Security security = securityService.retrieveRandomSecurity();

        switch (security.getSecurityType()) {
            case "STOCK":
                System.out.println("STOCK update process starting...");
                return new PriceUpdateEvent(security.getTickerId(), BigDecimal.valueOf(Math.random() * 200));
            case "PUT":
                System.out.println("PUT Option update process starting...");
                return new PriceUpdateEvent(security.getTickerId(), BigDecimal.valueOf(Math.random() * 200));
            case "CALL":
                System.out.println("CALL Option update process starting...");
                return new PriceUpdateEvent(security.getTickerId(), BigDecimal.valueOf(Math.random() * 200));
            default:
                System.out.println("UnsupportedSecurityType, Skipping update process");
                return new PriceUpdateEvent();
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

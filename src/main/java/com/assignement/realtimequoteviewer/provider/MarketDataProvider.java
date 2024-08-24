package com.assignement.realtimequoteviewer.provider;


import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import com.assignement.realtimequoteviewer.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

            long timeInterval = sleeper();
            if (marketTimer()) {

                PriceUpdateEvent priceUpdateEvent = createPriceUpdateEvent(timeInterval);
                if (this.priceUpdateChannel.offer(priceUpdateEvent)) {
                    System.out.println("/!\\----------[Market Data Provider] Sending Market Data update: " + priceUpdateEvent + " ----------/!\\");
                } else {
                    System.out.println("/!\\/!\\ ---------- Price Channel full, unable to insert price update " + priceUpdateEvent + ". ----------/!\\/!\\");
                }
            }
        }
    }

    private PriceUpdateEvent createPriceUpdateEvent(long timeInterval) {
        Security security = securityService.retrieveRandomSecurity();

        switch (security.getSecurityType()) {
            case "STOCK":
                System.out.println("STOCK update process starting...");
                System.out.println("Last Traded price for " + security.getTickerId() + ": " + security.getLastStockPrice());
                BigDecimal newPrice = calculateStockPrice(security.getLastStockPrice(), security.getStockReturn(), security.getAnnualStdDev(), timeInterval);
                securityService.updateLastPrice(security.getTickerId(), newPrice);
                System.out.println("Updated Traded price for " + security.getTickerId() + ": " + newPrice.round(new MathContext(5)));
                return new PriceUpdateEvent(security.getTickerId(), newPrice);
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

    private BigDecimal calculateStockPrice( double lastStockPriceValue, double annualReturnValue, double annualReturnStdDevValue, long timeIntervalMillis) {

        Random randomGenerator = new Random();
        BigDecimal randomValue = BigDecimal.valueOf(randomGenerator.nextGaussian());
        BigDecimal annualReturn = BigDecimal.valueOf(annualReturnValue);
        BigDecimal annualReturnStdDev = BigDecimal.valueOf(annualReturnStdDevValue);
        BigDecimal timeInterval = BigDecimal.valueOf(timeIntervalMillis).divide(BigDecimal.valueOf(1000),1000, RoundingMode.HALF_DOWN);

        BigDecimal annualReturnComponent = annualReturn.multiply(timeInterval.divide(BigDecimal.valueOf(7257600), 10, RoundingMode.HALF_DOWN));

        BigDecimal sqrtTimeValue = BigDecimal.valueOf(Math.sqrt(timeInterval.divide(BigDecimal.valueOf(7257600), 10, RoundingMode.HALF_DOWN).doubleValue()));
        BigDecimal annualStdDevComponent = annualReturnStdDev.multiply(randomValue).multiply(sqrtTimeValue);

        BigDecimal lastStockPrice = BigDecimal.valueOf(lastStockPriceValue);
        BigDecimal newStockPrice = lastStockPrice.add(lastStockPrice.multiply(annualReturnComponent.add(annualStdDevComponent)));

        return BigDecimal.ZERO.max(newStockPrice);
    }

    private long sleeper() {
        Random random = new Random();
        double randomTimer = random.nextDouble(0.5, 2) * 1000;
        try {
            long timer = (long) randomTimer;
            Thread.sleep(timer);
            return timer;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean marketTimer() {
        Random random = new Random();

        return random.nextBoolean();
    }
}

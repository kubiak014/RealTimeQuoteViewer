package com.assignement.realtimequoteviewer.provider;


import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import com.assignement.realtimequoteviewer.service.CalculationService;
import com.assignement.realtimequoteviewer.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MarketDataProvider {
    private final Logger logger = LoggerFactory.getLogger(MarketDataProvider.class);

    private final BlockingQueue<PriceUpdateEvent> priceUpdateChannel;

    @Autowired
    private SecurityService securityService;

    private CalculationService calculationService;

    public MarketDataProvider(BlockingQueue<PriceUpdateEvent> priceUpdateChannel, SecurityService securityService) {
        this.priceUpdateChannel = priceUpdateChannel;
        this.securityService = securityService;
        this.calculationService = new CalculationService(securityService);
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
        this.calculationService = new CalculationService(securityService);
    }

    public MarketDataProvider(BlockingQueue<PriceUpdateEvent> priceUpdateChannel, SecurityService securityService, CalculationService calculationService) {
        this.priceUpdateChannel = priceUpdateChannel;
        this.securityService = securityService;
        this.calculationService = calculationService;
    }

    public void runMarketDataProvider() {
        while (true) {
            long timeInterval = sleeper();
            if (marketTimer()) {
                PriceUpdateEvent priceUpdateEvent = createPriceUpdateEvent(timeInterval);
                if(priceUpdateEvent != null) {
                    if (!this.priceUpdateChannel.offer(priceUpdateEvent)) {
                        this.logger.error("/!\\/!\\ ---------- Price Channel full, unable to insert price update " + priceUpdateEvent + ". ----------/!\\/!\\");
                    }
                }else{
                    this.logger.error("An error Occurred while creating Price Update Event.");
                }
            }
        }
    }

    PriceUpdateEvent createPriceUpdateEvent(long timeInterval) {
        Security security = securityService.retrieveRandomStockSecurity();
        if (security != null) {
            BigDecimal undlNewSpotPrice = calculationService.calculateStockPrice(security.getLastStockPrice(), security.getStockReturn(), security.getAnnualStdDev(), timeInterval);
            if(undlNewSpotPrice != null) {
                return new PriceUpdateEvent(security.getTickerId(), undlNewSpotPrice);
            } else {
                this.logger.error("An error occured while getting new stock price.");
                return null;
            }

        } else {
            this.logger.error("Unable to retrieve a stock from referential.");
            return null;
        }

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

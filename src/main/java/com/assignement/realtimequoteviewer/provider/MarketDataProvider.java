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

    public void runMarketDataProvider() {
        while (true) {
            long timeInterval = sleeper();
            if (marketTimer()) {
                PriceUpdateEvent priceUpdateEvent = createPriceUpdateEvent(timeInterval);
                if (!this.priceUpdateChannel.offer(priceUpdateEvent)) {
                    this.logger.error("/!\\/!\\ ---------- Price Channel full, unable to insert price update " + priceUpdateEvent + ". ----------/!\\/!\\");
                }
            }
        }
    }

    private PriceUpdateEvent createPriceUpdateEvent(long timeInterval) {
        Security security = securityService.retrieveRandomStockSecurity();
        BigDecimal undlNewSpotPrice = calculationService.calculateStockPrice(security.getLastStockPrice(), security.getStockReturn(), security.getAnnualStdDev(), timeInterval);
        return new PriceUpdateEvent(security.getTickerId(), undlNewSpotPrice);

    }


//    private BigDecimal d1(BigDecimal newStockPrice , BigDecimal strike, BigDecimal annualRiskFreeRate, BigDecimal timeToExpiry, BigDecimal annualStockStdDev) {
//
//        BigDecimal strikePrice = newStockPrice.multiply(strike);
//
//        double top = Math.log(newStockPrice.divide(strikePrice,10, RoundingMode.HALF_DOWN ).doubleValue()) +
//                    (annualRiskFreeRate.add(annualStockStdDev.pow(2).divide(BigDecimal.valueOf(2),10, RoundingMode.HALF_DOWN) ) ).multiply(timeToExpiry).doubleValue();
//        double bottom = annualRiskFreeRate.multiply(BigDecimal.valueOf(Math.sqrt(timeToExpiry.doubleValue()))).doubleValue();
//
//        return BigDecimal.valueOf(top).divide(BigDecimal.valueOf(bottom),10, RoundingMode.HALF_DOWN);
//    }
//
//    private BigDecimal d2(BigDecimal newStockPrice , BigDecimal strike, BigDecimal annualRiskFreeRate, BigDecimal timeToExpiry, BigDecimal annualStockStdDev) {
//
//        BigDecimal d1 = d1(newStockPrice, strike, annualRiskFreeRate,timeToExpiry, annualStockStdDev);
//        return d1.subtract(annualStockStdDev.multiply(BigDecimal.valueOf(Math.sqrt(timeToExpiry.doubleValue()))));
//    }

//    private BigDecimal computePutOptionPrice(BigDecimal newStockPrice , BigDecimal strike, BigDecimal annualRiskFreeRate, BigDecimal timeToExpiry, BigDecimal annualStockStdDev) {
//
//        BigDecimal strikePrice = newStockPrice.multiply(strike);
//
////        BigDecimal d2 = d2(newStockPrice,strikePrice,annualRiskFreeRate, timeToExpiry, annualStockStdDev);
////        BigDecimal d1 = d1(newStockPrice,strikePrice,annualRiskFreeRate, timeToExpiry, annualStockStdDev);
////
////        BigDecimal timeRateValue = BigDecimal.valueOf(Math.exp(-1*annualRiskFreeRate.doubleValue()*timeToExpiry.doubleValue()));
//        double putPrice= BlackScholesFormula.calculate(false,newStockPrice.doubleValue(), strikePrice.doubleValue(),annualRiskFreeRate.doubleValue(), timeToExpiry.doubleValue(), annualStockStdDev.doubleValue());
//        return BigDecimal.valueOf(putPrice);
//
////        return strikePrice.multiply(timeRateValue).multiply(standardNormalDistribution(d2.multiply(BigDecimal.valueOf(-1)).doubleValue()))
////                .subtract(newStockPrice.multiply( standardNormalDistribution(d1.multiply(BigDecimal.valueOf(-1)).doubleValue())));
//    }

//    private BigDecimal computeCallOptionPrice(BigDecimal newStockPrice , BigDecimal strike, BigDecimal annualRiskFreeRate, BigDecimal timeToExpiry, BigDecimal annualStockStdDev) {
//
//        BigDecimal strikePrice = newStockPrice.multiply(strike);
////        BigDecimal d1 = d1(newStockPrice,strikePrice,annualRiskFreeRate, timeToExpiry, annualStockStdDev);
////        BigDecimal d2 = d2(newStockPrice,strikePrice,annualRiskFreeRate, timeToExpiry, annualStockStdDev);
////
////        BigDecimal timeRateValue = BigDecimal.valueOf(Math.exp(-1*annualRiskFreeRate.doubleValue()*timeToExpiry.doubleValue()));
//
//         double callPrice= BlackScholesFormula.calculate(true,newStockPrice.doubleValue(), strikePrice.doubleValue(),annualRiskFreeRate.doubleValue(), timeToExpiry.doubleValue(), annualStockStdDev.doubleValue());
//
//        return BigDecimal.valueOf(callPrice);
//        //newStockPrice.multiply(standardNormalDistribution(d1.doubleValue())).subtract(strikePrice.multiply(timeRateValue).multiply(standardNormalDistribution(d2.doubleValue())));
//    }

//    private BigDecimal standardNormalDistribution(double x) {
//
//        //System.out.println(" in BlackScholesFormula:standardNormalDistribution(" + x + ")");
//        double top = Math.exp(-0.5 * Math.pow(x, 2));
//        double bottom = Math.sqrt(2 * Math.PI);
//        double resp = top / bottom;
//
//        return BigDecimal.valueOf(resp);
//    }

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

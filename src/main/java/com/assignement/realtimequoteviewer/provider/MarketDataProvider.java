package com.assignement.realtimequoteviewer.provider;


import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import com.assignement.realtimequoteviewer.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
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
                if (!this.priceUpdateChannel.offer(priceUpdateEvent)) {
                    System.out.println("/!\\/!\\ ---------- Price Channel full, unable to insert price update " + priceUpdateEvent + ". ----------/!\\/!\\");
                }
            }
        }
    }

    private PriceUpdateEvent createPriceUpdateEvent(long timeInterval) {
        Security security = securityService.retrieveRandomStockSecurity();
        BigDecimal undlNewSpotPrice = calculateStockPrice(security.getLastStockPrice(), security.getStockReturn(), security.getAnnualStdDev(), timeInterval);

        securityService.updateLastStockPrice(security.getTickerId(), undlNewSpotPrice);
        return new PriceUpdateEvent(security.getTickerId(), undlNewSpotPrice);

    }

//    private BigDecimal calculateOptionPrice(BigDecimal newStockPrice, BigDecimal maturity, Double strikeValue, Double annualStdDevValue, String securityTypeValue) {
//
//        BigDecimal annualRiskFreeRate = BigDecimal.valueOf(0.02);
//        BigDecimal strike = BigDecimal.valueOf(strikeValue);
//        BigDecimal annualStdDev = BigDecimal.valueOf(annualStdDevValue);
//
//
//        if(securityTypeValue.equalsIgnoreCase("PUT")) {
//            return computePutOptionPrice(newStockPrice,strike,annualRiskFreeRate,maturity,annualStdDev);
//        }else if(securityTypeValue.equalsIgnoreCase("CALL")){
//            return computeCallOptionPrice(newStockPrice,strike,annualRiskFreeRate,maturity,annualStdDev);
//        }else{
//            System.out.println("Unsupported Type of security for option pricing: " + securityTypeValue);
//            return BigDecimal.ZERO;
//        }
//
//    }

//    private BigDecimal getTimeToExpiryYear(Security security) {
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        Date currentDate = calendar.getTime();
//
//        String expiryMonth = security.getTickerId().split("-")[1];
//        String expiryYear = security.getTickerId().split("-")[2];
//        calendar.set(Integer.parseInt(expiryYear), getMonthValue(expiryMonth), 1);
//        Date expiryDate = calendar.getTime();
//
//        float expiryInYears = (expiryDate.getTime() - currentDate.getTime()) / (1000f * 60 * 60 * 24 * 365);
//
//        return BigDecimal.valueOf(expiryInYears);
//
//    }

//    private int getMonthValue(String expiryMonth) {
//        Date date = null;
//        try {
//            date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(expiryMonth);
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        return cal.get(Calendar.MONTH);
//    }


    private BigDecimal calculateStockPrice(double lastStockPriceValue, double annualReturnValue, double annualReturnStdDevValue, long timeIntervalMillis) {

        Random randomGenerator = new Random();
        BigDecimal randomValue = BigDecimal.valueOf(randomGenerator.nextGaussian());
        BigDecimal annualReturn = BigDecimal.valueOf(annualReturnValue);
        BigDecimal annualReturnStdDev = BigDecimal.valueOf(annualReturnStdDevValue);
        BigDecimal timeInterval = BigDecimal.valueOf(timeIntervalMillis).divide(BigDecimal.valueOf(1000), 1000, RoundingMode.HALF_DOWN);

        BigDecimal annualReturnComponent = annualReturn.multiply(timeInterval.divide(BigDecimal.valueOf(7257600), 10, RoundingMode.HALF_DOWN));

        BigDecimal sqrtTimeValue = BigDecimal.valueOf(Math.sqrt(timeInterval.divide(BigDecimal.valueOf(7257600), 10, RoundingMode.HALF_DOWN).doubleValue()));
        BigDecimal annualStdDevComponent = annualReturnStdDev.multiply(randomValue).multiply(sqrtTimeValue);

        BigDecimal lastStockPrice = BigDecimal.valueOf(lastStockPriceValue);
        BigDecimal newStockPrice = lastStockPrice.add(lastStockPrice.multiply(annualReturnComponent.add(annualStdDevComponent)));

        return BigDecimal.ZERO.max(newStockPrice).setScale(4, RoundingMode.HALF_DOWN);
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

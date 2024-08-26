package com.assignement.realtimequoteviewer.service;

import com.assignement.realtimequoteviewer.loader.PositionLoader;
import com.assignement.realtimequoteviewer.model.Asset;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.pricing.BlackScholesFormula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import static com.assignement.realtimequoteviewer.utils.CalendarUtils.getOptionTimeToExpiryYear;

public class CalculationService {

    private final Logger logger = LoggerFactory.getLogger(PositionLoader.class);

    private SecurityService securityService;

    public CalculationService() {
    }

    public CalculationService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public Portfolio calculateInitialPortfolioNav(Portfolio portfolio) {

        portfolio.getAssets().forEach(asset -> {
            String assetTicker = asset.getTicker();
            Security tobeUpdated = this.securityService.retrieveSecurityByTickerID(assetTicker);
            BigDecimal newUndlPrice = BigDecimal.valueOf(tobeUpdated.getLastTradedPrice());
            updatePortfolioValue(asset, newUndlPrice, tobeUpdated);

        });
        portfolio.calculatePortfolioNAV();
        return portfolio;
    }

    public void updatePortfolioValue(Asset asset, BigDecimal newUndlPrice, Security tobeUpdated) {
        long quantity = asset.getQuantity().longValue();
        double newAssetPrice = 0.0;

        BigDecimal annualRiskFreeRate = BigDecimal.valueOf(0.02);


        if (tobeUpdated.getSecurityType().equals("STOCK")) {
            asset.setAssetValue(BigDecimal.valueOf(newUndlPrice.doubleValue() * quantity).setScale(2, RoundingMode.HALF_DOWN));
            this.securityService.updateLastStockPrice(asset.getTicker(), BigDecimal.valueOf(newUndlPrice.doubleValue()).setScale(4, RoundingMode.HALF_DOWN));
        } else {

            if (tobeUpdated.getSecurityType().equals("PUT")) {
                BigDecimal timeToExpiry = getOptionTimeToExpiryYear(tobeUpdated);
                double strikePrice = newUndlPrice.doubleValue() * tobeUpdated.getStrike();
                newAssetPrice = BlackScholesFormula.calculate(false, newUndlPrice.doubleValue(), strikePrice, annualRiskFreeRate.doubleValue(), timeToExpiry.doubleValue(), tobeUpdated.getAnnualStdDev());

            } else if (tobeUpdated.getSecurityType().equals("CALL")) {
                BigDecimal timeToExpiry = getOptionTimeToExpiryYear(tobeUpdated);
                double strikePrice = newUndlPrice.doubleValue() * tobeUpdated.getStrike();
                newAssetPrice = BlackScholesFormula.calculate(true, newUndlPrice.doubleValue(), strikePrice, annualRiskFreeRate.doubleValue(), timeToExpiry.doubleValue(), tobeUpdated.getAnnualStdDev());
            } else {
                this.logger.error("Unsupported Security Type, no calculation performed for " + tobeUpdated.getTickerId() + ":" + tobeUpdated.getSecurityType());
                return;
            }

            this.securityService.updateLastStockPrice(asset.getTicker(), newUndlPrice.setScale(4, RoundingMode.HALF_DOWN));
            this.securityService.updateOptionPrice(asset.getTicker(), BigDecimal.valueOf(newAssetPrice).setScale(4, RoundingMode.HALF_DOWN), newUndlPrice.setScale(4, RoundingMode.HALF_DOWN));
            asset.setAssetValue(BigDecimal.valueOf(newAssetPrice * quantity).setScale(2, RoundingMode.HALF_DOWN));
        }

    }

    public BigDecimal calculateStockPrice(double lastStockPriceValue, double annualReturnValue, double annualReturnStdDevValue, long timeIntervalMillis) {

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

}

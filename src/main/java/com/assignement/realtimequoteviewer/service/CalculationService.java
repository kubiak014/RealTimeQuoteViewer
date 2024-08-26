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
        this.logger.info("Calculating Initial Portfolio Value:");
        portfolio.getAssets().forEach(asset -> {
            String assetTicker = asset.getTicker();
            Security tobeUpdated = this.securityService.retrieveSecurityByTickerID(assetTicker);
            asset.setAssetValue(BigDecimal.valueOf(tobeUpdated.getLastTradedPrice() * asset.getQuantity().doubleValue()));

        });
        portfolio.calculatePortfolioNAV();
        return portfolio;
    }

    public void updateAssetValue(Asset asset, BigDecimal newUndlPrice, Security tobeUpdated) {
        BigDecimal quantity = BigDecimal.valueOf(asset.getQuantity().longValue());
        BigDecimal newAssetPrice;
        BigDecimal annualRiskFreeRate = BigDecimal.valueOf(0.02);

        if (tobeUpdated.getSecurityType().equals("STOCK")) {
            asset.setAssetValue(newUndlPrice.multiply(quantity).setScale(2, RoundingMode.HALF_DOWN));
            this.securityService.updateLastStockPrice(asset.getTicker(), newUndlPrice);
        } else {

            if (tobeUpdated.getSecurityType().equals("PUT")) {
                BigDecimal timeToExpiry = getOptionTimeToExpiryYear(tobeUpdated);
                double strikePrice = getOptionStrikePrice(asset);
                newAssetPrice = BlackScholesFormula.calculate(false, newUndlPrice.doubleValue(), strikePrice, annualRiskFreeRate.doubleValue(), timeToExpiry.doubleValue(), tobeUpdated.getAnnualStdDev());

            } else if (tobeUpdated.getSecurityType().equals("CALL")) {
                BigDecimal timeToExpiry = getOptionTimeToExpiryYear(tobeUpdated);
                double strikePrice = getOptionStrikePrice(asset);
                newAssetPrice = BlackScholesFormula.calculate(true, newUndlPrice.doubleValue(), strikePrice, annualRiskFreeRate.doubleValue(), timeToExpiry.doubleValue(), tobeUpdated.getAnnualStdDev());
            } else {
                this.logger.error("Unsupported Security Type, no calculation performed for " + tobeUpdated.getTickerId() + ":" + tobeUpdated.getSecurityType());
                return;
            }

            this.securityService.updateLastStockPrice(asset.getTicker(), newUndlPrice);
            this.securityService.updateOptionPrice(asset.getTicker(), newAssetPrice, newUndlPrice);
            asset.setAssetValue(newAssetPrice.multiply(quantity).setScale(2, RoundingMode.HALF_DOWN));
        }

    }

    private static double getOptionStrikePrice(Asset asset) {

        return Double.parseDouble(asset.getTicker().split("-")[3]);
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

        return BigDecimal.ZERO.max(newStockPrice).setScale(6, RoundingMode.HALF_DOWN);
    }

}

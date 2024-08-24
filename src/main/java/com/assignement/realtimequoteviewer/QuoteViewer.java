package com.assignement.realtimequoteviewer;

import com.assignement.realtimequoteviewer.loader.PositionLoader;
import com.assignement.realtimequoteviewer.model.Asset;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.pricing.BlackScholesFormula;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import com.assignement.realtimequoteviewer.service.SecurityService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class QuoteViewer {

    private BlockingQueue<PriceUpdateEvent> priceUpdateChannel;
    private Portfolio portfolio;

    private SecurityService securityService;

    QuoteViewer(String positionFilePath) {
        this.portfolio = PositionLoader.loadPortfolioFromExtract(positionFilePath);
        this.priceUpdateChannel = new LinkedBlockingQueue<>();
    }

    private void initPortfolioValue() {
        this.portfolio.getAssets().forEach(asset -> {
            String assetTicker = asset.getTicker();
            Security tobeUpdated = this.securityService.retrieveSecurityByTickerID(assetTicker);
            updatePortfolioValue(asset, BigDecimal.valueOf(tobeUpdated.getLastStockPrice()), tobeUpdated);

        });
        printPortfolioValue();

    }

    public QuoteViewer(String portfolioExtractPath, BlockingQueue<PriceUpdateEvent> priceUpdateChannel) {
        this(portfolioExtractPath);
        this.priceUpdateChannel = priceUpdateChannel;
    }

    public QuoteViewer(String portfolioExtractPath, BlockingQueue<PriceUpdateEvent> priceUpdateChannel, SecurityRepository securityRepository) {
        this(portfolioExtractPath);
        this.priceUpdateChannel = priceUpdateChannel;
        this.securityService = new SecurityService(securityRepository);
    }

    public void start() {

        //Print initial position loaded
        initPortfolioValue();

        //while loop to display position details initially and monitor marketDataUpdates
        while (true) {
            //pretty print portfolio
            monitorMarketUpdate();
        }
    }


    private void printPortfolioValue() {
        System.out.println();
        System.out.format("%-25s %10s %40s%n", "symbol", "qty", "value");
        this.portfolio.getAssets().forEach(asset -> System.out.format("%-25s %10s %40s%n", asset.getTicker(), asset.getQuantity(), asset.getAssetValue()));
        double portfolioNAV = this.portfolio.getAssets().stream()
                .mapToDouble(asset -> asset.getAssetValue().doubleValue())
                .sum();
        System.out.println();
        System.out.format("%-35s %40s%n", "#Total Portfolio", portfolioNAV);
        System.out.println();
    }

    private void monitorMarketUpdate() {

        // if update exist, update/print new portfolio valuation
        if (this.priceUpdateChannel.peek() != null) {
            PriceUpdateEvent priceUpdate = this.priceUpdateChannel.poll();
            System.out.println("/!\\---------- Market Data update Received " + priceUpdate + ", starting processing ----------/!\\");

            List<Asset> assetsToBeUpdated = portfolio.getAssets().stream().filter(asset -> asset.getTicker().contains(priceUpdate.getTickerID())).collect(Collectors.toList());
            assetsToBeUpdated.forEach(asset -> {
                String assetTicker = asset.getTicker();
                BigDecimal newUndlPrice = priceUpdate.getNewPrice();

                Security tobeUpdated = this.securityService.retrieveSecurityByTickerID(assetTicker);
                updatePortfolioValue(asset, newUndlPrice, tobeUpdated);

            });

            printPortfolioValue();
        }
    }

    private void updatePortfolioValue(Asset asset, BigDecimal newUndlPrice, Security tobeUpdated) {
        if (tobeUpdated.getSecurityType().equals("STOCK")) {
            double newStockPrice = newUndlPrice.doubleValue();
            long quantity = asset.getQuantity().longValue();
            asset.setAssetValue(BigDecimal.valueOf(newStockPrice * quantity));
            this.securityService.updateLastStockPrice(asset.getTicker(), BigDecimal.valueOf(newStockPrice));

        } else if (tobeUpdated.getSecurityType().equals("PUT")) {
            BigDecimal annualRiskFreeRate = BigDecimal.valueOf(0.02);
            double strikePrice = newUndlPrice.doubleValue() * tobeUpdated.getStrike();
            BigDecimal timeToExpiry = getTimeToExpiryYear(tobeUpdated);
            double putPrice = BlackScholesFormula.calculate(false, newUndlPrice.doubleValue(), strikePrice, annualRiskFreeRate.doubleValue(), timeToExpiry.doubleValue(), tobeUpdated.getAnnualStdDev());
            long quantity = asset.getQuantity().longValue();
            asset.setAssetValue(BigDecimal.valueOf(putPrice * quantity));
            this.securityService.updateLastStockPrice(asset.getTicker(), newUndlPrice);
            this.securityService.updateOptionPrice(asset.getTicker(), BigDecimal.valueOf(putPrice), newUndlPrice);


        } else if (tobeUpdated.getSecurityType().equals("CALL")) {

            BigDecimal annualRiskFreeRate = BigDecimal.valueOf(0.02);
            double strikePrice = newUndlPrice.doubleValue() * tobeUpdated.getStrike();
            BigDecimal timeToExpiry = getTimeToExpiryYear(tobeUpdated);
            double callPrice = BlackScholesFormula.calculate(true, newUndlPrice.doubleValue(), strikePrice, annualRiskFreeRate.doubleValue(), timeToExpiry.doubleValue(), tobeUpdated.getAnnualStdDev());
            long quantity = asset.getQuantity().longValue();
            asset.setAssetValue(BigDecimal.valueOf(callPrice * quantity));
            this.securityService.updateLastStockPrice(asset.getTicker(), newUndlPrice);
            this.securityService.updateOptionPrice(asset.getTicker(), BigDecimal.valueOf(callPrice), newUndlPrice);
        }
    }

    private BigDecimal getTimeToExpiryYear(Security security) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        Date currentDate = calendar.getTime();

        String expiryMonth = security.getTickerId().split("-")[1];
        String expiryYear = security.getTickerId().split("-")[2];
        calendar.set(Integer.parseInt(expiryYear), getMonthValue(expiryMonth), 1);
        Date expiryDate = calendar.getTime();

        float expiryInYears = (expiryDate.getTime() - currentDate.getTime()) / (1000f * 60 * 60 * 24 * 365);

        return BigDecimal.valueOf(expiryInYears);

    }

    private int getMonthValue(String expiryMonth) {
        Date date = null;
        try {
            date = new SimpleDateFormat("MMM", Locale.ENGLISH).parse(expiryMonth);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

}

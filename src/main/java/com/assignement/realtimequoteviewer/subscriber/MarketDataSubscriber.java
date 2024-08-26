package com.assignement.realtimequoteviewer.subscriber;

import com.assignement.realtimequoteviewer.model.Asset;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.service.CalculationService;
import com.assignement.realtimequoteviewer.service.SecurityService;
import com.assignement.realtimequoteviewer.utils.PrettyPrintUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

@Component
public class MarketDataSubscriber {

    @Getter
    @Setter
    private BlockingQueue<PriceUpdateEvent> priceUpdateChannel;

    @Getter
    @Autowired
    private SecurityService securityService;

    private CalculationService calculationService;

    public MarketDataSubscriber() {
    }

    public MarketDataSubscriber(BlockingQueue priceUpdateChannel, SecurityService securityService) {
        this.priceUpdateChannel = priceUpdateChannel;
        this.securityService = securityService;
        this.calculationService = new CalculationService(securityService);
    }

    public Portfolio monitorMarketUpdate(Portfolio portfolio) {

        // if update exist, update/print new portfolio valuation
        if (this.priceUpdateChannel.peek() != null) {
            PriceUpdateEvent priceUpdate = this.priceUpdateChannel.poll();
            System.out.println("/!\\---------- Market Data update Received " + priceUpdate + ", starting processing ----------/!\\");

            List<Asset> assetsToBeUpdated = portfolio.getAssets().stream().filter(asset -> asset.getTicker().contains(priceUpdate.getTickerID())).collect(Collectors.toList());
            assetsToBeUpdated.forEach(asset -> {
                String assetTicker = asset.getTicker();
                BigDecimal newUndlPrice = priceUpdate.getNewPrice();
                Security tobeUpdated = this.securityService.retrieveSecurityByTickerID(assetTicker);
                calculationService.updatePortfolioValue(asset, newUndlPrice, tobeUpdated);

            });
            PrettyPrintUtils.printPortfolio(portfolio);
        }
        portfolio.calculatePortfolioNAV();

        return portfolio;
    }


}

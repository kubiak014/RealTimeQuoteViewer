package com.assignement.realtimequoteviewer;

import com.assignement.realtimequoteviewer.loader.PositionLoader;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.service.CalculationService;
import com.assignement.realtimequoteviewer.service.SecurityService;
import com.assignement.realtimequoteviewer.subscriber.MarketDataSubscriber;
import com.assignement.realtimequoteviewer.utils.PrettyPrintUtils;

import java.util.concurrent.BlockingQueue;

public class QuoteViewer {

    private MarketDataSubscriber marketDataSubscriber;
    private Portfolio portfolio;

    private CalculationService calculationService;


    QuoteViewer(String positionFilePath) {
        this.portfolio = PositionLoader.loadPortfolioFromExtract(positionFilePath);
    }

    private void calculatePortfolioNavFromLastPrices() {
    }

    public QuoteViewer(String portfolioExtractPath, BlockingQueue<PriceUpdateEvent> priceUpdateChannel, SecurityService securityService) {
        this(portfolioExtractPath);
        this.marketDataSubscriber = new MarketDataSubscriber(priceUpdateChannel, securityService);
        this.calculationService = new CalculationService(securityService);
    }

    public void start() {
        calculateInitialPortfolioValue();

        //while loop to display position details initially and monitor marketDataUpdates
        while (true) {
            this.portfolio = marketDataSubscriber.monitorMarketUpdate(this.portfolio);

        }
    }

    private void calculateInitialPortfolioValue() {
        this.portfolio = calculationService.calculateInitialPortfolioNav(this.portfolio);
        PrettyPrintUtils.printPortfolio(this.portfolio);
    }


}

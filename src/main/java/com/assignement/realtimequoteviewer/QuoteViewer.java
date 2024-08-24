package com.assignement.realtimequoteviewer;

import com.assignement.realtimequoteviewer.loader.PositionLoader;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QuoteViewer {

    private BlockingQueue<PriceUpdateEvent> priceUpdateChannel;
    private Portfolio portfolio;

    QuoteViewer(String positionFilePath) {
        this.portfolio = PositionLoader.loadPortfolioFromExtract(positionFilePath);
        this.priceUpdateChannel = new LinkedBlockingQueue<>();
    }

    public QuoteViewer(String portfolioExtractPath, BlockingQueue<PriceUpdateEvent> priceUpdateChannel) {
        this(portfolioExtractPath);
        this.priceUpdateChannel = priceUpdateChannel;
    }

    public void start() {

        //Print initial position loaded
        printPortfolioValue();

        //while loop to display position details initially and monitor marketDataUpdates
        while (true) {
            //pretty print portfolio
            monitorMarketUpdate();
        }
    }


    private void printPortfolioValue() {
        System.out.format("%-25s %10s%n", "symbol", "qty");
        this.portfolio.getAssets().forEach(asset -> System.out.format("%-25s %10d%n", asset.getTicker(), asset.getQuantity()));
    }

    private void monitorMarketUpdate() {

        //TODO: if update exist, print new portfolio valuation
        if (this.priceUpdateChannel.peek() != null) {
            Object priceUpdate = this.priceUpdateChannel.poll();
            System.out.println("/!\\---------- Market Data update Detected, starting processing ----------/!\\");
        }
    }
}

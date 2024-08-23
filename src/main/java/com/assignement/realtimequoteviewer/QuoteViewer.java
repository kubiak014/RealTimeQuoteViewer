package com.assignement.realtimequoteviewer;

import com.assignement.realtimequoteviewer.loader.PositionLoader;
import com.assignement.realtimequoteviewer.model.Portfolio;

import java.util.Random;

public class QuoteViewer {

    private Portfolio portfolio;

    QuoteViewer(String positionFilePath) {
        this.portfolio = PositionLoader.loadPortfolioFromExtract(positionFilePath);
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
        Random random = new Random();
        float timer = (float) (random.nextFloat((int) (2 - 0.5 + 1)) + 0.5);
        try {
            Thread.sleep((long) (timer * 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(random.nextBoolean()) {
            System.out.println("/!\\---------- Market Data update Detected ----------/!\\");
            printPortfolioValue();
        }

    }
}

package com.assignement.realtimequoteviewer.utils;

import com.assignement.realtimequoteviewer.model.Portfolio;

public class PrettyPrintUtils {

    public static void printPortfolio(Portfolio portfolio) {
        System.out.print(String.format("\033[2J"));
        System.out.println();
        System.out.format("%-20s %10s %20s%n", "symbol", "qty", "value");
        portfolio.getAssets().forEach(asset -> System.out.format("%-20s %10s %20s%n", asset.getTicker(), asset.getQuantity(), asset.getAssetValue()));
        System.out.println();
        System.out.format("%-30s %21s%n", "#Total Portfolio", portfolio.getPortfolioNav());
        System.out.println();
    }
}

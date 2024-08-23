package com.assignement.realtimequoteviewer.loader;

import com.assignement.realtimequoteviewer.model.Portfolio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PositionLoaderTests {

    @Test
    public void testLoadPositionGivenEmptyPositionReturnEmptyPortfolio(){
        String emptyPortfolioPath = "./testFiles/emptyPortfolioPosition.csv";

        Portfolio portfolio = PositionLoader.loadPortfolioFromExtract(emptyPortfolioPath);
        assertEquals(0, portfolio.getAssets().size());

    }

    @Test
    public void testLoadPositionGivenNonExistingPositionReturnEmptyPortfolio(){
        String nullPortfolio = "./testFiles/notSupposedToExist.csv";
        Portfolio portfolio = PositionLoader.loadPortfolioFromExtract(nullPortfolio);
        assertNull(portfolio);

    }

    @Test
    public void testLoadPositionGivenExistingPositionReturnEmptyPortfolio(){
        String portfolioPath = "./testFiles/portfolioPosition.csv";
        Portfolio portfolio = PositionLoader.loadPortfolioFromExtract(portfolioPath);
        assertEquals(6, portfolio.getAssets().size());

    }
}

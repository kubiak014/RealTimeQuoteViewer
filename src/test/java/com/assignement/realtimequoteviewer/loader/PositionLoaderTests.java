package com.assignement.realtimequoteviewer.loader;

import com.assignement.realtimequoteviewer.model.Portfolio;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class PositionLoaderTests {

    @Test
    public void testLoadPositionGivenEmptyPositionReturnEmptyPortfolio(){
        String emptyPortfolioPath = "./testFiles/emptyPortfolioPosition.csv";
        PositionLoader positionLoader = new PositionLoader(emptyPortfolioPath);

        Portfolio portfolio = positionLoader.loadPortfolioFromExtract();
        assertEquals(0, portfolio.getAssets().size());

    }

    @Test
    public void testLoadPositionGivenNonExistingPositionReturnEmptyPortfolio(){
        String emptyPortfolioPath = "./testFiles/notSupposedToExist.csv";
        PositionLoader positionLoader = new PositionLoader(emptyPortfolioPath);

        Portfolio portfolio = positionLoader.loadPortfolioFromExtract();
        assertNull(portfolio);

    }

    @Test
    public void testLoadPositionGivenExistingPositionReturnEmptyPortfolio(){
        String emptyPortfolioPath = "./testFiles/portfolioPosition.csv";
        PositionLoader positionLoader = new PositionLoader(emptyPortfolioPath);

        Portfolio portfolio = positionLoader.loadPortfolioFromExtract();
        assertEquals(6, portfolio.getAssets().size());

    }
}

package com.assignement.realtimequoteviewer;

import com.assignement.realtimequoteviewer.loader.PositionLoader;
import com.assignement.realtimequoteviewer.model.Portfolio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;

@Component
public class QuoteViewer {

    private Portfolio portfolio;
    private final PositionLoader positionLoader;

    @Autowired
    QuoteViewer(PositionLoader positionLoader) {
        this.positionLoader = positionLoader;
        this.portfolio = this.positionLoader.loadPortfolioFromExtract();
    }
    public void start() {


    }
}

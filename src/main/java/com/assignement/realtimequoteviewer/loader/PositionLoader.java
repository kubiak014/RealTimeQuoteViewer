package com.assignement.realtimequoteviewer.loader;

import com.assignement.realtimequoteviewer.RealTimeQuoteViewerApplication;
import com.assignement.realtimequoteviewer.model.Asset;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PositionLoader {
    static Logger logger = LoggerFactory.getLogger(PositionLoader.class);

    public static Portfolio loadPortfolioFromExtract(String extractPath) {
        List<Asset> portfolio = new ArrayList<>();
        try {

            FileReader filereader = new FileReader(extractPath);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();

            // print Data
            for (String[] row : allData) {
                Asset asset = new Asset(row[0], row[1]);
                portfolio.add(asset);
            }
        } catch (Exception e) {
            logger.error("Unable to find portfolio import file: " + extractPath);
            return null;

        }

        return new Portfolio(portfolio);
    }
}

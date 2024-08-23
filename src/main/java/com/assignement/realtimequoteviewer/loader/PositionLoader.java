package com.assignement.realtimequoteviewer.loader;

import com.assignement.realtimequoteviewer.model.Asset;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PositionLoader {

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
            System.out.println("Unable to find portfolio import file: " + extractPath);
            return null;

        }

        return new Portfolio(portfolio);
    }
}

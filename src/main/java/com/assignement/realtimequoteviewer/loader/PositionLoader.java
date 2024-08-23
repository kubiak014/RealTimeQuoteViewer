package com.assignement.realtimequoteviewer.loader;

import com.assignement.realtimequoteviewer.model.Asset;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PositionLoader {

    @Value("${loader.position.path}")
    private String positionFilePath;

    @Autowired
    public PositionLoader(){}

    public PositionLoader(String positionFilePath){
        this.positionFilePath = positionFilePath;
    }

    public Portfolio loadPortfolioFromExtract(){
        List<Asset> portfolio = new ArrayList<>();
        try {

            FileReader filereader = new FileReader(this.positionFilePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader)
                    .withSkipLines(1)
                    .build();
            List<String[]> allData = csvReader.readAll();

            // print Data
            for (String[] row : allData) {
                Asset asset = new Asset(row[0],row[1]);
                portfolio.add(asset);
            }
        }
        catch (Exception e) {
            System.out.println("Unable to find portfolio import file: " + this.positionFilePath);
            return null;

        }

        return new Portfolio(portfolio);
    }
}

package com.assignement.realtimequoteviewer.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    @Getter
    @Setter
    private List<Asset> assets;

    @Getter
    private BigDecimal portfolioNav;

    public Portfolio() {
        this.assets = new ArrayList<>();
        this.portfolioNav = BigDecimal.ZERO;
    }

    public Portfolio(List<Asset> assets) {
        this.assets = assets;
    }

    public void calculatePortfolioNAV() {

        this.portfolioNav = BigDecimal.valueOf(this.assets.stream()
                .mapToDouble(asset -> asset.getAssetValue().doubleValue())
                .sum()).setScale(2, RoundingMode.HALF_DOWN);

    }

}

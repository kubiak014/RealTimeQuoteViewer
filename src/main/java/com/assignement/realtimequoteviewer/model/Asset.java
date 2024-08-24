package com.assignement.realtimequoteviewer.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Asset {

    @Getter
    @Setter
    private String ticker;

    @Getter
    @Setter
    private BigInteger quantity;

    @Getter
    @Setter
    private BigDecimal assetValue;

    public Asset(String ticker, String quantity){
        this.ticker = ticker;
        this.quantity = new BigInteger(quantity);
    }
}

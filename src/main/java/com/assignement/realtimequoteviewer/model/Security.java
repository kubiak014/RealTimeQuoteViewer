package com.assignement.realtimequoteviewer.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Security {

    @Id
    @Getter
    private String tickerId;

    @Getter
    @Setter
    private Double lastStockPrice;

    @Getter
    private String securityType;
    @Getter
    private Double stockReturn;
    @Getter
    private Double annualStdDev;
    @Getter
    private Double strike;
    @Getter
    private Integer maturity;

    public Security(){}

    public Security(String tickerId, String securityType) {
        this.tickerId = tickerId;
        this.securityType = securityType;
    }

    public Security(String tickerId, Double lastStockPrice, String securityType, Double stockReturn, Double annualStdDev, Double strike, Integer maturity) {
        this.tickerId = tickerId;
        this.lastStockPrice = lastStockPrice;
        this.securityType = securityType;
        this.stockReturn = stockReturn;
        this.annualStdDev = annualStdDev;
        this.strike = strike;
        this.maturity = maturity;
    }

    @Override
    public String toString() {
        return "Security{" +
                "tickerId='" + tickerId + '\'' +
                ", lastStockPrice=" + lastStockPrice +
                ", securityType='" + securityType + '\'' +
                ", stockReturn=" + stockReturn +
                ", annualStdDev=" + annualStdDev +
                ", strike=" + strike +
                ", maturity=" + maturity +
                '}';
    }
}

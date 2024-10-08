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
    @Setter
    private Double lastTradedPrice;

    @Getter
    private String securityType;
    @Getter
    private Double stockReturn;
    @Getter
    private Double annualStdDev;
    @Getter
    private Integer maturity;

    public Security() {
    }

    public Security(String tickerId, String securityType) {
        this.tickerId = tickerId;
        this.securityType = securityType;
    }

    public Security(String tickerId, Double lastStockPrice, Double lastTradedPrice, String securityType, Double stockReturn, Double annualStdDev, Integer maturity) {
        this.tickerId = tickerId;
        this.lastStockPrice = lastStockPrice;
        this.lastTradedPrice = lastTradedPrice;
        this.securityType = securityType;
        this.stockReturn = stockReturn;
        this.annualStdDev = annualStdDev;
        this.maturity = maturity;
    }

    @Override
    public String toString() {
        return "Security{" +
                "tickerId='" + tickerId + '\'' +
                ", lastStockPrice=" + lastStockPrice +
                ", lastTradedPrice=" + lastTradedPrice +
                ", securityType='" + securityType + '\'' +
                ", stockReturn=" + stockReturn +
                ", annualStdDev=" + annualStdDev +
                ", maturity=" + maturity +
                '}';
    }
}

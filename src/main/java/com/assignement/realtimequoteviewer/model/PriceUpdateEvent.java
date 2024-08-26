package com.assignement.realtimequoteviewer.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class PriceUpdateEvent {

    @Getter
    @Setter
    private String tickerID;

    @Getter
    @Setter
    private BigDecimal newPrice;

    @Getter
    private LocalDateTime updateDatetime;

    public PriceUpdateEvent() {
        this.tickerID = "UNKNOWN";
        this.newPrice = BigDecimal.ZERO;
        this.updateDatetime = LocalDateTime.of(LocalDate.of(1900, 1, 1), LocalTime.MIDNIGHT);
    }

    public PriceUpdateEvent(String tickerID, BigDecimal newPrice) {
        this.tickerID = tickerID;
        this.newPrice = newPrice;
        this.updateDatetime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "PriceUpdateEvent{" +
                "tickerID='" + tickerID + '\'' +
                ", newPrice=" + newPrice +
                ", updateDatetime=" + updateDatetime +
                '}';
    }
}

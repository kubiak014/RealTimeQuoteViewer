package com.assignement.realtimequoteviewer.model;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Security {

    @Id
    @Getter
    private String tickerId;

    @Getter
    private String securityType;

    public Security(){}

    public Security(String tickerId, String securityType) {
        this.tickerId = tickerId;
        this.securityType = securityType;
    }

    @Override
    public String toString() {
        return "{" +
                "tickerId='" + tickerId + '\'' +
                ", securityType='" + securityType + '\'' +
                '}';
    }
}

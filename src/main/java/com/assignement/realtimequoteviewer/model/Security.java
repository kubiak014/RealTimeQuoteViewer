package com.assignement.realtimequoteviewer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Security {

    @Id
    private String tickerId;

    private String securityType;

    @Override
    public String toString() {
        return "{" +
                "tickerId='" + tickerId + '\'' +
                ", securityType='" + securityType + '\'' +
                '}';
    }
}

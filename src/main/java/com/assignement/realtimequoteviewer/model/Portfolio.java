package com.assignement.realtimequoteviewer.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Portfolio {

    @Getter
    @Setter
    private List<Asset> assets;
    public Portfolio(){
        this.assets = new ArrayList<>();
    }

    public Portfolio(List<Asset> assets){
        this.assets = assets;
    }

}

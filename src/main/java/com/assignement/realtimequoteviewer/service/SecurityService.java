package com.assignement.realtimequoteviewer.service;

import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SecurityService {

    private Map<String, Security> securityCache;

    private SecurityRepository securityRepository;

    public SecurityService() {
        this.securityCache = new HashMap<>();
    }

    @Autowired
    public SecurityService(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
        this.securityCache = new HashMap<>();
    }

    public Security retrieveSecurityByTickerID(String tickerID) {

        if (this.securityCache.containsKey(tickerID)) return this.securityCache.get(tickerID);

        Security targetSecurity = this.securityRepository.findByTickerId(tickerID);
        if (targetSecurity != null) {
            this.securityCache.put(tickerID, targetSecurity);
            return targetSecurity;
        } else {
            System.out.println("Ticker ID {" + tickerID + "} NOT FOUND.");
            return null;
        }
    }

    public Security retrieveRandomStockSecurity() {
        List<Security> securities = this.securityRepository.findAllBySecurityType("STOCK");
        int securitiesSize = securities.size();

        Security randomSecurity = securities.get((int) (Math.random() * securitiesSize));
        this.securityCache.put(randomSecurity.getTickerId(), randomSecurity);

        return randomSecurity;
    }

    public void updateLastStockPrice(String tickerId, BigDecimal newPrice) {

        Security tobeUpdated = this.securityRepository.findByTickerId(tickerId);
        tobeUpdated.setLastStockPrice(newPrice.doubleValue());
        tobeUpdated.setLastTradedPrice(newPrice.doubleValue());
        this.securityRepository.save(tobeUpdated);
        this.securityCache.put(tobeUpdated.getTickerId(), tobeUpdated);

    }

    public void updateOptionPrice(String tickerId, BigDecimal newPrice, BigDecimal newStockPrice) {

        Security tobeUpdated = this.securityRepository.findByTickerId(tickerId);
        tobeUpdated.setLastStockPrice(newStockPrice.doubleValue());
        tobeUpdated.setLastTradedPrice(newPrice.doubleValue());
        this.securityRepository.save(tobeUpdated);
        this.securityCache.put(tobeUpdated.getTickerId(), tobeUpdated);

    }
}

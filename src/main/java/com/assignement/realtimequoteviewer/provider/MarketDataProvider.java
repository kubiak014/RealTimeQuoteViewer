package com.assignement.realtimequoteviewer.provider;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class MarketDataProvider {

    @Autowired public MarketDataProvider (){

    }

    public void runMarketDataProvider(){
        //TODO: if update exist, print new portfolio valuation
        while(true) {
            Random random = new Random();
            float timer = (float) (random.nextFloat((int) (2 - 0.5 + 1)) + 0.5);
            try {
                Thread.sleep((long) (timer * 1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (random.nextBoolean()) {
                System.out.println("/!\\----------[Market Data Provider] Market Data update Detected ----------/!\\");
            }
        }
    }
}

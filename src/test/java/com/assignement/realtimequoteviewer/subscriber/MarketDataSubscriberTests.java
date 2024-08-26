package com.assignement.realtimequoteviewer.subscriber;

import com.assignement.realtimequoteviewer.model.Asset;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.service.CalculationService;
import com.assignement.realtimequoteviewer.service.SecurityService;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.Port;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class MarketDataSubscriberTests {

    @Test
    public void testMonitorMarketUpdateProcessingNewUpdates(){

        BlockingQueue<PriceUpdateEvent> priceUpdateChannel = new LinkedBlockingQueue<>();
        PriceUpdateEvent dummyPriceUpdateEvent = new PriceUpdateEvent("AAPL", BigDecimal.valueOf(100.00));
        priceUpdateChannel.offer(dummyPriceUpdateEvent);

        Security dummySecurity = mock(Security.class);
        when(dummySecurity.getLastTradedPrice()).thenReturn(100.00);

        Asset dummyAsset = new Asset("AAPL", "100");
        dummyAsset.setAssetValue(BigDecimal.valueOf(10000.00));

        List<Asset> assets = new ArrayList<>();
        assets.add(dummyAsset);

        SecurityService mockSecurityService = mock(SecurityService.class);
        when(mockSecurityService.retrieveSecurityByTickerID(anyString())).thenReturn(dummySecurity);

        CalculationService mockCalculationService = mock(CalculationService.class);
        doNothing().when(mockCalculationService).updateAssetValue(any(Asset.class), any(BigDecimal.class), any(Security.class));

        Portfolio portfolio = new Portfolio(assets);

        MarketDataSubscriber marketDataSubscriber = new MarketDataSubscriber(priceUpdateChannel,mockSecurityService, mockCalculationService);

        Portfolio result = marketDataSubscriber.monitorMarketUpdate(portfolio);
        assertEquals(result, portfolio);

    }
}

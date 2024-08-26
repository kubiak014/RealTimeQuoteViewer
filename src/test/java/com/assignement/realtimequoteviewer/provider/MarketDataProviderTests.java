package com.assignement.realtimequoteviewer.provider;

import com.assignement.realtimequoteviewer.model.PriceUpdateEvent;
import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import com.assignement.realtimequoteviewer.service.CalculationService;
import com.assignement.realtimequoteviewer.service.SecurityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MarketDataProviderTests {

    @Test
    public void testCreatePriceUpdateEventGivenExistingSecurityAndNewPriceReturnsPriceEvenCreated(){
        CalculationService mockCalculationService = mock(CalculationService.class);
        when(mockCalculationService.calculateStockPrice(anyDouble(), anyDouble(), anyDouble(), anyLong())).thenReturn(BigDecimal.valueOf(123.45));

        SecurityService mockSecurityService = mock(SecurityService.class);
        Security dummySecurity = mock(Security.class);
        when(dummySecurity.getLastStockPrice()).thenReturn(123.00);
        when(dummySecurity.getStockReturn()).thenReturn(0.02);
        when(dummySecurity.getAnnualStdDev()).thenReturn(0.23);

        when(mockSecurityService.retrieveRandomStockSecurity()).thenReturn(dummySecurity);

        MarketDataProvider marketDataProvider = new MarketDataProvider(null,mockSecurityService, mockCalculationService);

        PriceUpdateEvent result = marketDataProvider.createPriceUpdateEvent(1000);
        assertNotNull(result);
        assertEquals(123.45, result.getNewPrice().doubleValue());

    }

    @Test
    public void testCreatePriceUpdateEventGivenNoSecurityReturnsNullPriceEvent(){
        CalculationService mockCalculationService = mock(CalculationService.class);
        when(mockCalculationService.calculateStockPrice(anyDouble(), anyDouble(), anyDouble(), anyLong())).thenReturn(BigDecimal.valueOf(123.45));

        SecurityService mockSecurityService = mock(SecurityService.class);
        Security dummySecurity = mock(Security.class);
        when(dummySecurity.getLastStockPrice()).thenReturn(123.00);
        when(dummySecurity.getStockReturn()).thenReturn(0.02);
        when(dummySecurity.getAnnualStdDev()).thenReturn(0.23);

        when(mockSecurityService.retrieveRandomStockSecurity()).thenReturn(null);

        MarketDataProvider marketDataProvider = new MarketDataProvider(null,mockSecurityService, mockCalculationService);

        PriceUpdateEvent result = marketDataProvider.createPriceUpdateEvent(1000);
        assertNull(result);

    }

    @Test
    public void testCreatePriceUpdateEventGivenNullStockPriceReturnsNullPriceEvent(){
        CalculationService mockCalculationService = mock(CalculationService.class);
        when(mockCalculationService.calculateStockPrice(anyDouble(), anyDouble(), anyDouble(), anyLong())).thenReturn(null);

        SecurityService mockSecurityService = mock(SecurityService.class);
        Security dummySecurity = mock(Security.class);
        when(dummySecurity.getLastStockPrice()).thenReturn(123.00);
        when(dummySecurity.getStockReturn()).thenReturn(0.02);
        when(dummySecurity.getAnnualStdDev()).thenReturn(0.23);

        when(mockSecurityService.retrieveRandomStockSecurity()).thenReturn(dummySecurity);

        MarketDataProvider marketDataProvider = new MarketDataProvider(null,mockSecurityService, mockCalculationService);

        PriceUpdateEvent result = marketDataProvider.createPriceUpdateEvent(1000);
        assertNull(result);

    }
}

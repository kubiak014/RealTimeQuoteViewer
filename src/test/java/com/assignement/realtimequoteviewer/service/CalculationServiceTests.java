package com.assignement.realtimequoteviewer.service;

import com.assignement.realtimequoteviewer.model.Asset;
import com.assignement.realtimequoteviewer.model.Portfolio;
import com.assignement.realtimequoteviewer.model.Security;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CalculationServiceTests {

    @Test
    public void testCalculateInitialPortfolioNavGivenExistingPortfolioReturnNav(){

        SecurityService mockSecurityService = mock(SecurityService.class);
        Security dummySecurity = mock(Security.class);
        when(dummySecurity.getLastTradedPrice()).thenReturn(123.00);
        when(mockSecurityService.retrieveSecurityByTickerID(anyString())).thenReturn(dummySecurity);

        Asset dummyAsset = new Asset("DUMMY", "100");
        List<Asset> assets = new ArrayList<>();
        assets.add(dummyAsset);

        Portfolio portfolio = new Portfolio(assets);
        BigDecimal expected = BigDecimal.valueOf(12300.00).setScale(2, RoundingMode.HALF_DOWN);

        CalculationService calculationService = new CalculationService(mockSecurityService);
        Portfolio result = calculationService.calculateInitialPortfolioNav(portfolio);

        assertEquals(expected, result.getPortfolioNav());

    }

    @Test
    public void testUpdateAssetValueGivenExistingStockAssetDoesUpdateAssetValue(){

        SecurityService mockSecurityService = mock(SecurityService.class);
        Security dummySecurity = mock(Security.class);
        when(dummySecurity.getLastTradedPrice()).thenReturn(120.00);
        when(dummySecurity.getSecurityType()).thenReturn("STOCK");
        when(mockSecurityService.retrieveSecurityByTickerID(anyString())).thenReturn(dummySecurity);
        doNothing().when(mockSecurityService).updateLastStockPrice(anyString(), any(BigDecimal.class));

        Asset dummyAsset = new Asset("DUMMY", "100");
        dummyAsset.setAssetValue(BigDecimal.valueOf(12000));

        BigDecimal expected = BigDecimal.valueOf(12300.00).setScale(2, RoundingMode.HALF_DOWN);

        CalculationService calculationService = new CalculationService(mockSecurityService);
        calculationService.updateAssetValue(dummyAsset, BigDecimal.valueOf(123.00), dummySecurity);

        assertEquals(expected, dummyAsset.getAssetValue());

    }

    @Test
    public void testUpdateAssetValueGivenExistingCallAssetDoesUpdateAssetValue(){

        SecurityService mockSecurityService = mock(SecurityService.class);
        Security dummySecurity = mock(Security.class);
        when(dummySecurity.getLastTradedPrice()).thenReturn(120.00);
        when(dummySecurity.getSecurityType()).thenReturn("CALL");
        when(dummySecurity.getTickerId()).thenReturn("AAPL-OCT-2024-200-C");
        when(dummySecurity.getAnnualStdDev()).thenReturn(0.15);
        when(dummySecurity.getLastTradedPrice()).thenReturn(1.00);
        when(mockSecurityService.retrieveSecurityByTickerID(anyString())).thenReturn(dummySecurity);
        doNothing().when(mockSecurityService).updateLastStockPrice(anyString(), any(BigDecimal.class));
        doNothing().when(mockSecurityService).updateOptionPrice(anyString(), any(BigDecimal.class), any(BigDecimal.class));

        Asset dummyAsset = new Asset("AAPL-OCT-2024-200-C", "100");
        dummyAsset.setAssetValue(BigDecimal.valueOf(12000));

        BigDecimal lastTradedValue = BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_DOWN);

        CalculationService calculationService = new CalculationService(mockSecurityService);
        calculationService.updateAssetValue(dummyAsset, BigDecimal.valueOf(123.00), dummySecurity);

        assertNotEquals(dummyAsset.getAssetValue(), lastTradedValue);

    }

    @Test
    public void testUpdateAssetValueGivenExistingPutAssetDoesUpdateAssetValue(){

        SecurityService mockSecurityService = mock(SecurityService.class);
        Security dummySecurity = mock(Security.class);
        when(dummySecurity.getLastTradedPrice()).thenReturn(120.00);
        when(dummySecurity.getSecurityType()).thenReturn("PUT");
        when(dummySecurity.getTickerId()).thenReturn("AAPL-OCT-2024-200-P");
        when(dummySecurity.getAnnualStdDev()).thenReturn(0.15);
        when(dummySecurity.getLastTradedPrice()).thenReturn(1.00);
        when(mockSecurityService.retrieveSecurityByTickerID(anyString())).thenReturn(dummySecurity);
        doNothing().when(mockSecurityService).updateLastStockPrice(anyString(), any(BigDecimal.class));
        doNothing().when(mockSecurityService).updateOptionPrice(anyString(), any(BigDecimal.class), any(BigDecimal.class));

        Asset dummyAsset = new Asset("AAPL-OCT-2024-200-P", "100");
        dummyAsset.setAssetValue(BigDecimal.valueOf(12000));

        BigDecimal lastTradedValue = BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_DOWN);

        CalculationService calculationService = new CalculationService(mockSecurityService);
        calculationService.updateAssetValue(dummyAsset, BigDecimal.valueOf(123.00), dummySecurity);

        assertNotEquals(dummyAsset.getAssetValue(), lastTradedValue);

    }
}

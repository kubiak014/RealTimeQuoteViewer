package com.assignement.realtimequoteviewer.service;

import com.assignement.realtimequoteviewer.model.Security;
import com.assignement.realtimequoteviewer.repository.SecurityRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SecurityServiceTests {

    @Test
    public void testRetrieveSecurityByTickerIdGivenEmptyTickerReturnNull(){

        SecurityRepository mockSecurityRepository = mock(SecurityRepository.class);
        when(mockSecurityRepository.findByTickerId(null)).thenReturn(null);
        when(mockSecurityRepository.findByTickerId("")).thenReturn(null);

        SecurityService securityService = new SecurityService(mockSecurityRepository);
        Security result = securityService.retrieveSecurityByTickerID(null);
        assertNull(result);

        result = securityService.retrieveSecurityByTickerID("");
        assertNull(result);

    }

    @Test
    public void testRetrieveSecurityByTickerIdGivenTickerReturnNull(){

        Security dummySecurity = mock(Security.class);
        when(dummySecurity.getTickerId()).thenReturn("AAPL");
        SecurityRepository mockSecurityRepository = mock(SecurityRepository.class);
        when(mockSecurityRepository.findByTickerId("AAPL")).thenReturn(dummySecurity);

        SecurityService securityService = new SecurityService(mockSecurityRepository);
        Security result = securityService.retrieveSecurityByTickerID(null);
        assertNull(result);

        result = securityService.retrieveSecurityByTickerID("AAPL");
        assertNotNull(result);
        assertEquals("AAPL", result.getTickerId());

    }
}

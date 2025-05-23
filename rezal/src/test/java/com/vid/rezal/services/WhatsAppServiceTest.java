package com.vid.rezal.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class WhatsAppServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WhatsAppService whatsAppService;

    private final String testApiUrl = "https://fake.whatsapp.api/v1/messages";
    private final String testApiToken = "fake-token";
    private final String testPhoneNumber = "1234567890";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Use ReflectionTestUtils to set the @Value fields
        ReflectionTestUtils.setField(whatsAppService, "whatsappApiUrl", testApiUrl);
        ReflectionTestUtils.setField(whatsAppService, "whatsappApiToken", testApiToken);
    }

    @Test
    void sendTextMessage_success() {
        ResponseEntity<String> mockResponse = new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
        when(restTemplate.postForEntity(eq(testApiUrl), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        boolean result = whatsAppService.sendTextMessage(testPhoneNumber, "Hello");
        assertTrue(result, "sendTextMessage should return true on success");
    }

    @Test
    void sendTextMessage_failure() {
        when(restTemplate.postForEntity(eq(testApiUrl), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("API Error"));

        boolean result = whatsAppService.sendTextMessage(testPhoneNumber, "Hello");
        assertFalse(result, "sendTextMessage should return false on API error");
    }

    @Test
    void sendTemplateMessage_success() {
        ResponseEntity<String> mockResponse = new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
        when(restTemplate.postForEntity(eq(testApiUrl), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        boolean result = whatsAppService.sendTemplateMessage(testPhoneNumber, "test_template", Collections.emptyList());
        assertTrue(result, "sendTemplateMessage should return true on success");
    }

    @Test
    void sendTemplateMessage_failure() {
        when(restTemplate.postForEntity(eq(testApiUrl), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("API Error"));

        boolean result = whatsAppService.sendTemplateMessage(testPhoneNumber, "test_template", Collections.emptyList());
        assertFalse(result, "sendTemplateMessage should return false on API error");
    }
    
    @Test
    void sendFlowMessage_success() {
        ResponseEntity<String> mockResponse = new ResponseEntity<>("{\"status\":\"success\"}", HttpStatus.OK);
        when(restTemplate.postForEntity(eq(testApiUrl), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponse);

        boolean result = whatsAppService.sendFlowMessage(testPhoneNumber, "flow_id", "flow_token", "screen_id", Collections.emptyMap());
        assertTrue(result, "sendFlowMessage should return true on success");
    }

    @Test
    void sendFlowMessage_failure() {
        when(restTemplate.postForEntity(eq(testApiUrl), any(HttpEntity.class), eq(String.class)))
                .thenThrow(new RuntimeException("API Error"));

        boolean result = whatsAppService.sendFlowMessage(testPhoneNumber, "flow_id", "flow_token", "screen_id", Collections.emptyMap());
        assertFalse(result, "sendFlowMessage should return false on API error");
    }
}

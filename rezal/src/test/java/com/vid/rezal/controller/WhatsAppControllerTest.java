package com.vid.rezal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vid.rezal.model.Customer;
import com.vid.rezal.model.ViewData;
import com.vid.rezal.services.ServiceLocator;
import com.vid.rezal.services.WhatsAppService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq; // Make sure eq is imported
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WhatsAppController.class)
class WhatsAppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WhatsAppService whatsAppService;

    @MockBean
    private ServiceLocator serviceLocator;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON strings

    // This field needs to be initialized in the controller instance for the test to work as expected.
    // One way is to have a test application.properties or use ReflectionTestUtils if the controller is auto-instantiated.
    @Autowired // Autowire the controller to set its @Value field
    private WhatsAppController whatsAppController;


    private Customer testCustomer;
    private ViewData<Customer> customerViewData;
    private final String testVerifyToken = "test-webhook-token";

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setName("Test User");
        testCustomer.setWhatsappPhoneNumber("1234567890");

        customerViewData = new ViewData<>();
        customerViewData.setData(testCustomer);
        
        // Mock the behavior of serviceLocator.getCustomerByName
        when(serviceLocator.getCustomerByName(anyString())).thenReturn(customerViewData);
        when(serviceLocator.getCustomerByName(eq("Test User"))).thenReturn(customerViewData); // More specific mock for "Test User"


        // Set the webhookVerifyToken for the controller instance using ReflectionTestUtils
        ReflectionTestUtils.setField(whatsAppController, "webhookVerifyToken", testVerifyToken);
    }

    @Test
    void verifyWebhook_success() throws Exception {
        mockMvc.perform(get("/whatsapp/webhook")
                        .param("hub.mode", "subscribe")
                        .param("hub.challenge", "challenge_code")
                        .param("hub.verify_token", testVerifyToken)) 
                .andExpect(status().isOk())
                .andExpect(content().string("challenge_code"));
    }
    
    @Test
    void verifyWebhook_failure() throws Exception {
        mockMvc.perform(get("/whatsapp/webhook")
                        .param("hub.mode", "subscribe")
                        .param("hub.challenge", "challenge_code")
                        .param("hub.verify_token", "wrong_token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void receiveWhatsAppMessage_logsPayload() throws Exception {
        String testPayload = "{\"object\":\"whatsapp_business_account\",\"entry\":[]}"; // Corrected JSON array
        mockMvc.perform(post("/whatsapp/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testPayload))
                .andExpect(status().isOk())
                .andExpect(content().string("Message received"));
        // Further verification could involve checking logs if a test appender is configured.
    }

    @Test
    void sendWhatsAppTextMessage_success() throws Exception {
        when(whatsAppService.sendTextMessage(eq(testCustomer.getWhatsappPhoneNumber()), anyString())).thenReturn(true);

        mockMvc.perform(post("/whatsapp/sendText")
                        .param("customerName", testCustomer.getName())
                        .param("messageText", "Hello Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("WhatsApp text message initiated to " + testCustomer.getName()));
    }

    @Test
    void sendWhatsAppTextMessage_customerNotFound() throws Exception {
        ViewData<Customer> notFoundViewData = new ViewData<>();
        notFoundViewData.setError("Customer not found");
        // Ensure this specific mock is effective:
        when(serviceLocator.getCustomerByName(eq("Unknown"))).thenReturn(notFoundViewData);


        mockMvc.perform(post("/whatsapp/sendText")
                        .param("customerName", "Unknown")
                        .param("messageText", "Hello Test"))
                .andExpect(status().isOk()) // ViewData wrapper usually results in 200 OK even for logical errors
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    void sendWhatsAppTextMessage_serviceFailure() throws Exception {
        when(whatsAppService.sendTextMessage(eq(testCustomer.getWhatsappPhoneNumber()), anyString())).thenReturn(false);

        mockMvc.perform(post("/whatsapp/sendText")
                        .param("customerName", testCustomer.getName())
                        .param("messageText", "Hello Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Failed to send WhatsApp text message to " + testCustomer.getName()));
    }

    @Test
    void sendWhatsAppTemplateMessage_success() throws Exception {
        List<String> components = Collections.singletonList("component1");
        when(whatsAppService.sendTemplateMessage(eq(testCustomer.getWhatsappPhoneNumber()), anyString(), any(List.class))).thenReturn(true);

        mockMvc.perform(post("/whatsapp/sendTemplate")
                        .param("customerName", testCustomer.getName())
                        .param("templateName", "test_template")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(components)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("WhatsApp template message 'test_template' initiated to " + testCustomer.getName()));
    }
    
    @Test
    void sendWhatsAppFlowMessage_success() throws Exception {
        Map<String, Object> flowData = Map.of("key", "value");
        when(whatsAppService.sendFlowMessage(eq(testCustomer.getWhatsappPhoneNumber()), anyString(), anyString(), anyString(), any(Map.class))).thenReturn(true);

        mockMvc.perform(post("/whatsapp/sendFlow")
                        .param("customerName", testCustomer.getName())
                        .param("flowId", "test_flow_id")
                        .param("flowToken", "test_flow_token")
                        .param("screenId", "test_screen_id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(flowData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("WhatsApp Flow message (flowId: test_flow_id) initiated to " + testCustomer.getName()));
    }
}

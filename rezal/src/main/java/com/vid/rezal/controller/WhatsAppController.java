package com.vid.rezal.controller;

import com.vid.rezal.model.ViewData; // Assuming ViewData is a generic response wrapper
import com.vid.rezal.services.WhatsAppService;
import com.vid.rezal.services.ServiceLocator; // To find customer by name for sending
import com.vid.rezal.model.Customer; // To get whatsappPhoneNumber

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/whatsapp") // Base path for WhatsApp related endpoints
public class WhatsAppController {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppController.class);

    @Autowired
    private WhatsAppService whatsappService;

    @Autowired
    private ServiceLocator serviceLocator; // For fetching customer details

    @Value("${whatsapp.webhook.verify_token}") // For webhook verification
    private String webhookVerifyToken;

    // Endpoint for WhatsApp to verify the webhook
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token) {
        
        logger.info("Received webhook verification request. Mode: {}, Token: {}, Challenge: {}", mode, token, challenge);
        if ("subscribe".equals(mode) && webhookVerifyToken.equals(token)) {
            logger.info("Webhook verification successful.");
            return ResponseEntity.ok(challenge);
        } else {
            logger.warn("Webhook verification failed. Mode or token mismatch.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Webhook verification failed");
        }
    }

    // Endpoint to receive messages from WhatsApp (webhook)
    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWhatsAppMessage(@RequestBody String payload) {
        // WhatsApp sends notifications as JSON.
        // The structure of this payload needs to be handled according to WhatsApp API documentation.
        // For now, we just log it.
        // In a real application, you would parse this JSON to extract message details,
        // sender's number, message type, content, etc.
        // You might also need to verify the signature of the request for security.
        logger.info("Received WhatsApp message payload: {}", payload);
        
        // Process the payload here (e.g., save to DB, trigger auto-reply via WhatsAppService)
        // For example, if it's a text message:
        // String sender = ... extract from payload ...;
        // String messageText = ... extract from payload ...;
        // logger.info("Message from {}: {}", sender, messageText);

        return ResponseEntity.ok("Message received");
    }

    // Endpoint to send a text message via WhatsApp
    @PostMapping("/sendText")
    public ViewData<String> sendWhatsAppTextMessage(
            @RequestParam String customerName, // Or directly use whatsappPhoneNumber
            @RequestParam String messageText) {
        ViewData<String> response = new ViewData<>();
        ViewData<Customer> customerView = serviceLocator.getCustomerByName(customerName); // Use existing service to get customer
        
        if (customerView.getData() != null && customerView.getData().getWhatsappPhoneNumber() != null) {
            Customer customer = customerView.getData();
            boolean success = whatsappService.sendTextMessage(customer.getWhatsappPhoneNumber(), messageText);
            if (success) {
                response.setData("WhatsApp text message initiated to " + customerName);
            } else {
                response.setError("Failed to send WhatsApp text message to " + customerName);
                response.setResponse(com.vid.rezal.model.Statics.FAILURE); // Assuming Statics.FAILURE exists
            }
        } else {
            response.setError("Customer or WhatsApp phone number not found for: " + customerName);
            response.setResponse(com.vid.rezal.model.Statics.FAILURE);
        }
        return response;
    }

    // Endpoint to send a template message via WhatsApp
    @PostMapping("/sendTemplate")
    public ViewData<String> sendWhatsAppTemplateMessage(
            @RequestParam String customerName, // Or directly use whatsappPhoneNumber
            @RequestParam String templateName,
            @RequestBody List<String> components) { // Components as JSON list in request body
        ViewData<String> response = new ViewData<>();
        ViewData<Customer> customerView = serviceLocator.getCustomerByName(customerName);

        if (customerView.getData() != null && customerView.getData().getWhatsappPhoneNumber() != null) {
            Customer customer = customerView.getData();
            boolean success = whatsappService.sendTemplateMessage(customer.getWhatsappPhoneNumber(), templateName, components);
            if (success) {
                response.setData("WhatsApp template message '" + templateName + "' initiated to " + customerName);
            } else {
                response.setError("Failed to send WhatsApp template message to " + customerName);
                response.setResponse(com.vid.rezal.model.Statics.FAILURE);
            }
        } else {
            response.setError("Customer or WhatsApp phone number not found for: " + customerName);
            response.setResponse(com.vid.rezal.model.Statics.FAILURE);
        }
        return response;
    }
    
    // Endpoint to send a flow message via WhatsApp
    // This is a simplified version. Actual flow parameters can be complex.
    @PostMapping("/sendFlow")
    public ViewData<String> sendWhatsAppFlowMessage(
            @RequestParam String customerName,
            @RequestParam String flowId,
            @RequestParam String flowToken, // This token might be generated dynamically per session
            @RequestParam String screenId,
            @RequestBody Map<String, Object> data) { // Flow data as JSON object in request body
        ViewData<String> response = new ViewData<>();
        ViewData<Customer> customerView = serviceLocator.getCustomerByName(customerName);

        if (customerView.getData() != null && customerView.getData().getWhatsappPhoneNumber() != null) {
            Customer customer = customerView.getData();
            boolean success = whatsappService.sendFlowMessage(customer.getWhatsappPhoneNumber(), flowId, flowToken, screenId, data);
            if (success) {
                response.setData("WhatsApp Flow message (flowId: " + flowId + ") initiated to " + customerName);
            } else {
                response.setError("Failed to send WhatsApp Flow message to " + customerName);
                response.setResponse(com.vid.rezal.model.Statics.FAILURE);
            }
        } else {
            response.setError("Customer or WhatsApp phone number not found for: " + customerName);
            response.setResponse(com.vid.rezal.model.Statics.FAILURE);
        }
        return response;
    }
}

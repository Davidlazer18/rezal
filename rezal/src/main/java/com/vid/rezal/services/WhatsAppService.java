package com.vid.rezal.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Service
public class WhatsAppService {

    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);

    @Value("${whatsapp.api.url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.api.token}")
    private String whatsappApiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendTextMessage(String toPhoneNumber, String messageText) {
        // Placeholder for actual WhatsApp API integration for sending a text message
        // This will involve constructing a JSON payload specific to the WhatsApp API provider
        // (e.g., Meta API)
        // Example structure (highly dependent on the specific API):
        /*
        {
            "messaging_product": "whatsapp",
            "to": "{toPhoneNumber}",
            "type": "text",
            "text": {
                "body": "{messageText}"
            }
        }
        */
        logger.info("Attempting to send WhatsApp text message to {}: {}", toPhoneNumber, messageText);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + whatsappApiToken);
        headers.set("Content-Type", "application/json");

        // This is a generic structure, actual payload will vary.
        // Using a simple map for the body for now.
        Map<String, Object> textObject = Map.of("body", messageText);
        Map<String, Object> requestBody = Map.of(
                "messaging_product", "whatsapp",
                "to", toPhoneNumber,
                "type", "text",
                "text", textObject
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // Example: Using a generic WhatsApp API URL. This needs to be specific to the provider.
            // e.g., for Meta: https://graph.facebook.com/v17.0/{PHONE_NUMBER_ID}/messages
            // The whatsappApiUrl should reflect this, including the sender's phone number ID.
            // For now, we'll assume whatsappApiUrl is the full base URL for sending messages.
            ResponseEntity<String> response = restTemplate.postForEntity(whatsappApiUrl, entity, String.class);
            logger.info("WhatsApp API Response: {}", response.getStatusCode());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error("Error sending WhatsApp message to {}: {}", toPhoneNumber, e.getMessage(), e);
            return false;
        }
    }

    public boolean sendTemplateMessage(String toPhoneNumber, String templateName, List<String> components) {
        // Placeholder for actual WhatsApp API integration for sending a template message
        logger.info("Attempting to send WhatsApp template message '{}' to {} with components: {}", templateName, toPhoneNumber, components);
        // Actual implementation will require constructing a specific JSON payload
        // for template messages, including language, components, etc.
        // Example structure (Meta API):
        /*
        {
            "messaging_product": "whatsapp",
            "to": "{toPhoneNumber}",
            "type": "template",
            "template": {
                "name": "{templateName}",
                "language": { "code": "en_US" }, // Or configurable
                "components": [ // Structure depends on template
                    // { "type": "body", "parameters": [ { "type": "text", "text": "value" } ] },
                    // { "type": "header", "parameters": [ { "type": "image", "image": { "link": "url" } } ] }
                ]
            }
        }
        */
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + whatsappApiToken);
        headers.set("Content-Type", "application/json");

        // Simplified payload for now
        Map<String, Object> templateDetails = Map.of(
            "name", templateName,
            "language", Map.of("code", "en_US") // Assuming English, make configurable if needed
            // "components" would be built dynamically based on the 'components' list
        );
         Map<String, Object> requestBody = Map.of(
                "messaging_product", "whatsapp",
                "to", toPhoneNumber,
                "type", "template",
                "template", templateDetails
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(whatsappApiUrl, entity, String.class);
            logger.info("WhatsApp Template API Response for {}: {}", toPhoneNumber, response.getStatusCode());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error("Error sending WhatsApp template message to {}: {}", toPhoneNumber, e.getMessage(), e);
            return false;
        }
    }

    public boolean sendFlowMessage(String toPhoneNumber, String flowId, String flowToken, String screenId, Map<String, Object> data) {
        // Placeholder for actual WhatsApp API integration for sending a flow message
        logger.info("Attempting to send WhatsApp flow message (flowId: {}) to {} with data: {}", flowId, toPhoneNumber, data);
        // Actual implementation will require constructing a specific JSON payload for flow messages.
        // This is highly dependent on the Flow's design and the WhatsApp API version.
        // It often involves an "interactive" message type.
        // Example (conceptual):
        /*
        {
            "messaging_product": "whatsapp",
            "recipient_type": "individual",
            "to": "{toPhoneNumber}",
            "type": "interactive",
            "interactive": {
                "type": "flow",
                "header": { // Optional
                    "type": "text",
                    "text": "Flow Title"
                },
                "body": {
                    "text": "Flow Body"
                },
                "footer": { // Optional
                    "text": "Flow Footer"
                },
                "action": {
                    "name": "flow",
                    "parameters": {
                        "flow_message_version": "3",
                        "flow_token": "{flowToken}", // A short-lived token
                        "flow_id": "{flowId}",
                        "flow_cta": "Go to Flow",
                        "flow_action": "navigate", // or "data_exchange"
                        "flow_action_payload": {
                            "screen": "{screenId}",
                            "data": { // Data to pre-fill or pass to the flow
                                ...data
                            }
                        }
                    }
                }
            }
        }
        */
         HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + whatsappApiToken);
        headers.set("Content-Type", "application/json");

        // This is a very conceptual and simplified payload
        Map<String, Object> flowActionPayload = Map.of(
            "screen", screenId,
            "data", data
        );
        Map<String, Object> flowAction = Map.of(
            "name", "flow",
            "parameters", Map.of(
                "flow_message_version", "3",
                "flow_token", flowToken,
                "flow_id", flowId,
                "flow_cta", "Open Flow", 
                "flow_action", "navigate",
                "flow_action_payload", flowActionPayload
            )
        );
        Map<String, Object> interactive = Map.of(
            "type", "flow",
            "action", flowAction
            // "header", "body", "footer" could be added here
        );
        Map<String, Object> requestBody = Map.of(
                "messaging_product", "whatsapp",
                "to", toPhoneNumber,
                "type", "interactive",
                "interactive", interactive
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(whatsappApiUrl, entity, String.class);
            logger.info("WhatsApp Flow API Response for {}: {}", toPhoneNumber, response.getStatusCode());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.error("Error sending WhatsApp flow message to {}: {}", toPhoneNumber, e.getMessage(), e);
            return false;
        }
    }
}

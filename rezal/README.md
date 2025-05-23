## WhatsApp Integration

The application supports sending and receiving messages via the WhatsApp Business API.

### Configuration

To enable WhatsApp integration, configure the following properties in your `application.properties` file:

-   `whatsapp.api.url`: The base URL for the WhatsApp Business API (e.g., Meta's Graph API endpoint like `https://graph.facebook.com/vXX.X/YOUR_PHONE_NUMBER_ID/messages`). Replace `YOUR_PHONE_NUMBER_ID` with your actual WhatsApp Business phone number ID.
-   `whatsapp.api.token`: Your WhatsApp Business API access token (Bearer token).
-   `whatsapp.webhook.verify_token`: A custom string token you create. This token is used to verify webhook registration requests from WhatsApp.

Example:
```properties
# WhatsApp Integration Configuration
whatsapp.api.url=https://graph.facebook.com/v17.0/YOUR_PHONE_NUMBER_ID/messages
whatsapp.api.token=YOUR_WHATSAPP_API_TOKEN
whatsapp.webhook.verify_token=YOUR_CUSTOM_WEBHOOK_VERIFY_TOKEN
```

### API Endpoints

All WhatsApp related endpoints are prefixed with `/whatsapp`.

#### Incoming Messages (Webhook)

-   **GET `/whatsapp/webhook`**:
    -   Used by WhatsApp for webhook verification.
    -   Requires query parameters: `hub.mode`, `hub.challenge`, `hub.verify_token`.
    -   Responds with `hub.challenge` if verification is successful.
-   **POST `/whatsapp/webhook`**:
    -   The endpoint WhatsApp will send message notifications to (e.g., new messages from users, status updates).
    -   Accepts a JSON payload from WhatsApp.
    -   Currently, logs the received payload. Further processing (parsing, storing, replying) would be an enhancement.

#### Outgoing Messages

-   **POST `/whatsapp/sendText`**:
    -   Sends a plain text message to a customer.
    -   Request parameters:
        -   `customerName` (String): Name of the customer (must exist in the system with a WhatsApp phone number).
        -   `messageText` (String): The text message to send.
    -   Response: `ViewData<String>` indicating success or failure.

-   **POST `/whatsapp/sendTemplate`**:
    -   Sends a pre-approved WhatsApp template message.
    -   Request parameters:
        -   `customerName` (String): Name of the customer.
        -   `templateName` (String): The name of the WhatsApp message template.
    -   Request body (JSON): `List<String>` representing the components/parameters for the template.
    -   Response: `ViewData<String>` indicating success or failure.

-   **POST `/whatsapp/sendFlow`**:
    -   Sends a WhatsApp Flow message.
    -   Request parameters:
        -   `customerName` (String): Name of the customer.
        -   `flowId` (String): The ID of the WhatsApp Flow.
        -   `flowToken` (String): A token for the flow session (often short-lived and dynamically generated).
        -   `screenId` (String): The initial screen ID within the flow.
    -   Request body (JSON): `Map<String, Object>` containing data to be passed to the flow.
    -   Response: `ViewData<String>` indicating success or failure.

### Setup
1.  Ensure the Customer entity includes a `whatsappPhoneNumber` field.
2.  Configure the `application.properties` as described above.
3.  Deploy the application.
4.  In your Meta Developer Portal (or other WhatsApp API provider's portal):
    -   Configure a webhook to point to `https://<your-app-domain>/whatsapp/webhook`.
    -   When configuring, use the same `verify_token` value that you set in `whatsapp.webhook.verify_token`.
```

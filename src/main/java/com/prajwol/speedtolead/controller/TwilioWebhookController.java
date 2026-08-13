package com.prajwol.speedtolead.controller;

import com.prajwol.speedtolead.service.ConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/twilio")
public class TwilioWebhookController {
    private static final Logger log = LoggerFactory.getLogger(TwilioWebhookController.class);
    private final ConversationService conv;

    public TwilioWebhookController(ConversationService conv) {
        this.conv = conv;
    }

    @PostMapping(value = "/inbound", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    public String inbound(@RequestParam Map<String, String> all) {
        String from = all.getOrDefault("From", all.get("from"));
        String body = all.getOrDefault("Body", all.get("body"));
        log.info("Received inbound SMS from {} body={}", from, body);
        conv.persistInbound(from, body, "sms");
        // respond with empty TwiML
        return "<Response></Response>";
    }
}

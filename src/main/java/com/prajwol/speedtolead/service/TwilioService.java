package com.prajwol.speedtolead.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class TwilioService {

    private final String accountSid = System.getenv("TWILIO_SID");
    private final String authToken = System.getenv("TWILIO_TOKEN");
    private final String fromNumber = System.getenv("TWILIO_FROM");
    private final RestTemplate rt = new RestTemplate();

    public String sendSms(String to, String body) {
        if (accountSid == null || authToken == null || fromNumber == null) {
            throw new IllegalStateException("Twilio credentials not configured in environment variables");
        }
        String url = String.format("https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json", accountSid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(accountSid, authToken);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("To", to);
        form.add("From", fromNumber);
        form.add("Body", body);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        ResponseEntity<String> resp = rt.postForEntity(url, request, String.class);
        if (resp.getStatusCode().is2xxSuccessful()) {
            return resp.getBody();
        } else {
            throw new RuntimeException("Twilio send failed: " + resp.getStatusCode().value() + " " + resp.getBody());
        }
    }
}
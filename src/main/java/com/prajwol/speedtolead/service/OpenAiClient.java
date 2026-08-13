package com.prajwol.speedtolead.service;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class OpenAiClient {
    private final String key = System.getenv("OPENAI_KEY");
    private final RestTemplate rt = new RestTemplate();

    public Map<String, Object> analyzeConversation(List<String> messages) {
        if (key == null) {
            return Map.of("outboundMessage", "", "leadStatus", "cold", "nextFollowupHours", 24, "nurtureNeeded", true);
        }
        try {
            String prompt = buildPrompt(messages);
            Map<String, Object> req = new HashMap<>();
            req.put("model", "gpt-4o-mini");
            req.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpHeaders h = new HttpHeaders();
            h.setBearerAuth(key);
            h.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map> r = rt.postForEntity("https://api.openai.com/v1/chat/completions", new HttpEntity<>(req, h), Map.class);
            if (r.getStatusCode().is2xxSuccessful() && r.getBody() != null) {
                // Extract assistant content
                List choices = (List) r.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map first = (Map) choices.get(0);
                    Map message = (Map) first.get("message");
                    String content = (String) message.get("content");
                    // Return content as outboundMessage and default other fields
                    return Map.of("outboundMessage", content, "leadStatus", "cold", "nextFollowupHours", 24, "nurtureNeeded", true);
                }
            }
        } catch (Exception e) {
            // swallow and return safe defaults
        }
        return Map.of("outboundMessage", "", "leadStatus", "cold", "nextFollowupHours", 24, "nurtureNeeded", true);
    }

    private String buildPrompt(List<String> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an assistant that classifies and responds to short real-estate lead SMS conversations. Given the last messages (oldest to newest) produce a JSON object with keys: outboundMessage (string), leadStatus ('hot' or 'cold'), nextFollowupHours (number), nurtureNeeded (true/false). Return only valid JSON. Messages:\n");
        for (String m : messages) {
            sb.append(m).append("\n");
        }
        sb.append("\nRespond with JSON only.");
        return sb.toString();
    }
}
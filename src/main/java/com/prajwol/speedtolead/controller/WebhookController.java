package com.prajwol.speedtolead.controller;

import com.prajwol.speedtolead.dto.RealEstateLead;
import com.prajwol.speedtolead.service.LeadService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private final LeadService leadService;

    public WebhookController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping(path = "/lead", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> receiveLead(@Valid @RequestBody RealEstateLead lead) {
        log.info("Received lead from webhook: {} {} - email={} phone={} source={} address={}",
                lead.getFirstName(), lead.getLastName(), lead.getEmail(), lead.getPhone(), lead.getSource(), lead.getPropertyAddress());

        leadService.saveLead(lead);

        return ResponseEntity.status(HttpStatus.CREATED).body("lead received and persisted");
    }
}
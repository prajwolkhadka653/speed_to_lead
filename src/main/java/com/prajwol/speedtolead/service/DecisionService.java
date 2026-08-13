package com.prajwol.speedtolead.service;

import com.prajwol.speedtolead.entity.Interaction;
import com.prajwol.speedtolead.entity.Lead;
import com.prajwol.speedtolead.repository.InteractionRepository;
import com.prajwol.speedtolead.repository.LeadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DecisionService {
    private static final Logger log = LoggerFactory.getLogger(DecisionService.class);
    private final OpenAiClient openAi;
    private final TwilioService twilio;
    private final LeadRepository leadRepo;
    private final InteractionRepository interactionRepo;

    public DecisionService(OpenAiClient openAi, TwilioService twilio, LeadRepository leadRepo, InteractionRepository interactionRepo) {
        this.openAi = openAi;
        this.twilio = twilio;
        this.leadRepo = leadRepo;
        this.interactionRepo = interactionRepo;
    }

    public void processLeadConversation(Lead lead) {
        try {
            List<Interaction> last = interactionRepo.findTop5ByLeadOrderByCreatedAtDesc(lead);
            List<String> msgs = new ArrayList<>();
            // reverse to chronological
            for (int i = last.size() - 1; i >= 0; i--) {
                Interaction it = last.get(i);
                msgs.add(it.getDirection() + ": " + it.getBody());
            }
            Map<String, Object> result = openAi.analyzeConversation(msgs);
            String outbound = Objects.toString(result.getOrDefault("outboundMessage", ""), "");
            String leadStatus = Objects.toString(result.getOrDefault("leadStatus", "cold"), "cold");
            Number hoursNum = (Number) result.getOrDefault("nextFollowupHours", 24);
            int hours = hoursNum == null ? 24 : hoursNum.intValue();
            boolean nurture = Boolean.parseBoolean(Objects.toString(result.getOrDefault("nurtureNeeded", "true")));

            lead.setStatus(leadStatus);
            leadRepo.save(lead);

            if ("hot".equalsIgnoreCase(leadStatus)) {
                log.info("Lead {} marked HOT — escalate to agent", lead.getId());
                // TODO: implement agent notification (email/Slack)
            } else {
                if (outbound != null && !outbound.isBlank()) {
                    try {
                        String resp = twilio.sendSms(lead.getPhone(), outbound);
                        // persist outbound interaction
                        com.prajwol.speedtolead.entity.Interaction out = new com.prajwol.speedtolead.entity.Interaction();
                        out.setLead(lead);
                        out.setBody(outbound);
                        out.setDirection("outbound");
                        out.setChannel("sms");
                        interactionRepo.save(out);
                        log.info("Sent outbound SMS to {}", lead.getPhone());
                    } catch (Exception ex) {
                        log.error("Failed to send SMS: {}", ex.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            log.error("Decision processing failed: {}", e.getMessage(), e);
        }
    }
}
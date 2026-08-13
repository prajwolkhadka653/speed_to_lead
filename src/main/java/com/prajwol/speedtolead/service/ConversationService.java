package com.prajwol.speedtolead.service;

import com.prajwol.speedtolead.entity.Interaction;
import com.prajwol.speedtolead.entity.Lead;
import com.prajwol.speedtolead.repository.InteractionRepository;
import com.prajwol.speedtolead.repository.LeadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {
    private static final Logger log = LoggerFactory.getLogger(ConversationService.class);
    private final LeadRepository leadRepo;
    private final InteractionRepository interactionRepo;
    private final DecisionService decisionService;

    public ConversationService(LeadRepository leadRepo, InteractionRepository interactionRepo, DecisionService decisionService) {
        this.leadRepo = leadRepo;
        this.interactionRepo = interactionRepo;
        this.decisionService = decisionService;
    }

    public Interaction persistInbound(String fromPhone, String body, String channel) {
        // find by phone or create a stub lead
        Lead lead = leadRepo.findByPhone(fromPhone).orElse(null);
        if (lead == null) {
            lead = new Lead();
            lead.setPhone(fromPhone);
            lead.setFirstName("Unknown");
            lead.setLastName("");
            lead.setEmail("");
            lead.setSource("twilio");
            lead.setStatus("new");
            lead = leadRepo.save(lead);
            log.info("Created stub lead for incoming phone {} with id {}", fromPhone, lead.getId());
        }

        Interaction in = new Interaction();
        in.setLead(lead);
        in.setBody(body);
        in.setDirection("inbound");
        in.setChannel(channel);
        interactionRepo.save(in);

        // process synchronously for MVP
        decisionService.processLeadConversation(lead);

        return in;
    }

    public List<Interaction> lastFive(Lead lead) {
        return interactionRepo.findTop5ByLeadOrderByCreatedAtDesc(lead);
    }
}

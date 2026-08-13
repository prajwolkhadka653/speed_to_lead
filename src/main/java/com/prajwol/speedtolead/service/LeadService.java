package com.prajwol.speedtolead.service;

import com.prajwol.speedtolead.dto.RealEstateLead;
import com.prajwol.speedtolead.entity.Lead;
import com.prajwol.speedtolead.repository.LeadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LeadService {

    private static final Logger log = LoggerFactory.getLogger(LeadService.class);
    private final LeadRepository leadRepository;

    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Lead saveLead(RealEstateLead leadDto) {
        Lead lead = new Lead();
        lead.setFirstName(leadDto.getFirstName());
        lead.setLastName(leadDto.getLastName());
        lead.setEmail(leadDto.getEmail());
        lead.setPhone(leadDto.getPhone());
        lead.setPropertyAddress(leadDto.getPropertyAddress());
        lead.setSource(leadDto.getSource());
        lead.setBudget(leadDto.getBudget());
        lead.setNotes(leadDto.getNotes());

        Lead savedLead = leadRepository.save(lead);
        log.info("Lead saved to database with ID: {}", savedLead.getId());
        return savedLead;
    }
}
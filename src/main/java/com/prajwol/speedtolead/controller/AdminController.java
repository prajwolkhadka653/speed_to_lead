package com.prajwol.speedtolead.controller;

import com.prajwol.speedtolead.entity.Interaction;
import com.prajwol.speedtolead.entity.Lead;
import com.prajwol.speedtolead.repository.InteractionRepository;
import com.prajwol.speedtolead.repository.LeadRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final LeadRepository leadRepo;
    private final InteractionRepository interactionRepo;

    public AdminController(LeadRepository leadRepo, InteractionRepository interactionRepo) {
        this.leadRepo = leadRepo;
        this.interactionRepo = interactionRepo;
    }

    @GetMapping("/leads")
    public Map<String, Object> recentLeads() {
        List<Lead> leads = leadRepo.findAll();
        Map<String, Object> m = new HashMap<>();
        m.put("count", leads.size());
        m.put("leads", leads);
        return m;
    }

    @GetMapping("/interactions")
    public Map<String, Object> recentInteractions() {
        List<Interaction> items = interactionRepo.findAll();
        Map<String, Object> m = new HashMap<>();
        m.put("count", items.size());
        m.put("interactions", items);
        return m;
    }
}

package com.prajwol.speedtolead.repository;

import com.prajwol.speedtolead.entity.Interaction;
import com.prajwol.speedtolead.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    List<Interaction> findTop5ByLeadOrderByCreatedAtDesc(Lead lead);
    List<Interaction> findByLeadOrderByCreatedAtDesc(Lead lead);
}
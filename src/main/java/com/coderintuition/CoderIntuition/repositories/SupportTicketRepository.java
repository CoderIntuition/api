package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
}


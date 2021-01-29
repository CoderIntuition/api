package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.CheckoutSession;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.ProblemStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheckoutSessionRepository extends JpaRepository<CheckoutSession, Long> {
    Optional<CheckoutSession> findBySessionId(String sessionId);
}

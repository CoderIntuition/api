package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.CheckoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckoutSessionRepository extends JpaRepository<CheckoutSession, Long> {

}

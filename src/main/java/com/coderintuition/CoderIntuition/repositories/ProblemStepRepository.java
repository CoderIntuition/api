package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.ProblemStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemStepRepository extends JpaRepository<ProblemStep, Long> {
}

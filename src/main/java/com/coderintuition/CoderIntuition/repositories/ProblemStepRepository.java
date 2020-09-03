package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.ProblemStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemStepRepository extends JpaRepository<ProblemStep, Long> {
    List<ProblemStep> findByProblemId(Long problemId);
}

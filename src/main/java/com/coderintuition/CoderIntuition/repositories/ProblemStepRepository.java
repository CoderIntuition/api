package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.ProblemStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemStepRepository extends JpaRepository<ProblemStep, Long> {
    Optional<ProblemStep> findByProblemAndStepNum(Problem problem, Integer stepNum);
}

package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.ProblemStep;
import com.coderintuition.CoderIntuition.models.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
    Optional<TestCase> findByProblemAndTestCaseNum(Problem problem, Integer testCaseNum);
}

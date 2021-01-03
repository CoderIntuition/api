package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
}


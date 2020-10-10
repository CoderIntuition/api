package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {
}

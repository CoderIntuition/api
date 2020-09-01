package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Testcase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestcaseRepository extends JpaRepository<Testcase, Long> {
}

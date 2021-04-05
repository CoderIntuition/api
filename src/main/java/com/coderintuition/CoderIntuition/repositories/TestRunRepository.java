package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.TestRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRunRepository extends JpaRepository<TestRun, Long> {
    Optional<TestRun> findByToken(String token);
}


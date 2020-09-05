package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.SubmissionRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRunRepository extends JpaRepository<SubmissionRun, Long> {
}


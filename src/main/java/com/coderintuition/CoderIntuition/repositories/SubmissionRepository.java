package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}


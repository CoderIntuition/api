package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
}


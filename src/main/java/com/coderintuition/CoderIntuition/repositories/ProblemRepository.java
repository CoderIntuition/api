package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByCategory(String category);
    Optional<Problem> findByUrlName(String urlName);
}

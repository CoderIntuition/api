package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
}

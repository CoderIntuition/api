package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.ProblemStep;
import com.coderintuition.CoderIntuition.models.Solution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SolutionRepository extends JpaRepository<Solution, Long> {
    Optional<Solution> findByProblemAndSolutionNum(Problem problem, Integer solutionNum);
}

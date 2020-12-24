package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Argument;
import com.coderintuition.CoderIntuition.models.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArgumentRepository extends JpaRepository<Argument, Long> {
    Optional<Argument> findByProblemAndArgumentNum(Problem problem, Integer argumentNum);
}

package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.ReturnType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReturnTypeRepository extends JpaRepository<ReturnType, Long> {
    Optional<ReturnType> findByProblem(Problem problem);
}

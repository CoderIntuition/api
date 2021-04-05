package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.ProduceOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProduceOutputRepository extends JpaRepository<ProduceOutput, Long> {
    Optional<ProduceOutput> findByToken(String token);
}


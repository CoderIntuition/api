package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.ProduceOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProduceOutputRepository extends JpaRepository<ProduceOutput, Long> {
    ProduceOutput findByToken(String token);
}


package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {
    Optional<Reading> findByUrlName(String urlName);
}

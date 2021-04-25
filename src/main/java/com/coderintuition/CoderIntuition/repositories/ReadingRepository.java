package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {
    Optional<Reading> findByUrlName(String urlName);

    @Query("SELECT r.urlName FROM Reading r WHERE r.plusOnly=FALSE")
    List<String> findAllPublicUrlNames();
}

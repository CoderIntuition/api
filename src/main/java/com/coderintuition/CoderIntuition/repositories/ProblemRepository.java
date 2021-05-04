package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.enums.ProblemCategory;
import com.coderintuition.CoderIntuition.models.Problem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    @Query("SELECT p.urlName FROM Problem p WHERE p.plusOnly=FALSE")
    List<String> findAllPublicUrlNames();

    @Query("SELECT p FROM Problem p WHERE p.category = (:category) AND (p.deleted IS NULL OR p.deleted = false) ORDER BY p.id ASC")
    Page<Problem> findByCategory(@Param("category") ProblemCategory problemCategory, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE (p.deleted IS NULL OR p.deleted = false) ORDER BY p.id DESC")
    Page<Problem> findAll(Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE p.category = (:category) AND (p.deleted IS NULL OR p.deleted = false) ORDER BY p.id ASC")
    List<Problem> findByCategory(@Param("category") ProblemCategory problemCategory);

    @Query("SELECT p FROM Problem p WHERE p.urlName = :urlName AND (p.deleted IS NULL OR p.deleted = false)")
    Optional<Problem> findByUrlName(@Param("urlName") String urlName);

    @Query("SELECT p FROM Problem p WHERE p.id = :id AND (p.deleted IS NULL OR p.deleted = false)")
    Optional<Problem> findById(@Param("id") Long id);

    @Query("SELECT p FROM Problem p WHERE (p.deleted IS NULL OR p.deleted = false) AND p.difficulty != 'BEGINNER' AND p.id NOT IN (:problemsSent)")
    List<Problem> findUnsentProblems(@Param("problemsSent") List<Long> problemsSent);

    @Query("SELECT p FROM Problem p WHERE (p.deleted IS NULL OR p.deleted = false) AND p.difficulty != 'BEGINNER'")
    List<Problem> findAllUnsentProblems();
}

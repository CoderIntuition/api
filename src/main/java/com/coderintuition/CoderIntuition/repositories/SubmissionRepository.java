package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Submission;
import com.coderintuition.CoderIntuition.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    @Query("SELECT COUNT(DISTINCT s.problem) FROM Submission s WHERE s.user = (:user) AND s.status = 'ACCEPTED'")
    int findNumOfCompletedProblemsByUser(@Param("user") User user);
}


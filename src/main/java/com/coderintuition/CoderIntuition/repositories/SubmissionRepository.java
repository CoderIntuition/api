package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.enums.Difficulty;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.Submission;
import com.coderintuition.CoderIntuition.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    Optional<Submission> findByToken(String token);

    @Query("SELECT COUNT(DISTINCT s.problem) FROM Submission s WHERE s.user = (:user) AND s.status = 'ACCEPTED'")
    int findNumOfCompletedProblemsByUser(@Param("user") User user);

    @Query("SELECT COUNT(DISTINCT s.problem) FROM Submission s JOIN Problem p ON s.problem = p " +
        "WHERE s.user = (:user) AND s.status = 'ACCEPTED' AND p.difficulty = (:difficulty)")
    int findNumOfCompletedProblemsByUserAndDifficulty(@Param("user") User user, @Param("difficulty")Difficulty difficulty);

    @Query("SELECT COUNT(DISTINCT s.id) FROM Submission s WHERE s.problem = :problem")
    int findNumCompletedSubmissionsByProblem(@Param("problem") Problem problem);
}


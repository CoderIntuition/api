package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.enums.ActivityType;
import com.coderintuition.CoderIntuition.models.Activity;
import com.coderintuition.CoderIntuition.models.Problem;
import com.coderintuition.CoderIntuition.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.user = (:user) AND a.problem = (:problem) AND a.activityType = (:activityType)")
    int findActivity(@Param("user") User user, @Param("problem") Problem problem, @Param("activityType") ActivityType activityType);

    @Query("SELECT a FROM Activity a WHERE a.user = (:user) ORDER BY a.created_at DESC")
    Page<Activity> findActivitiesByUser(@Param("user") User user, Pageable pageable);

    @Query("SELECT a FROM Activity a WHERE a.user = (:user)")
    List<Activity> findActivitiesByUser(@Param("user") User user);
}

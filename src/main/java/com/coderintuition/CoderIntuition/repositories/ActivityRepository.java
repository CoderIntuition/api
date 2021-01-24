package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

}

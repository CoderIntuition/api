package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeRepository extends JpaRepository<Badge, Long> {
}

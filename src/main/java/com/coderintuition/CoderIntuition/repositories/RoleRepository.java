package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.ERole;
import com.coderintuition.CoderIntuition.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}

package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUuid(String uuid);
    Optional<User> findByStripeCustomerId(String stripeCustomerId);
}

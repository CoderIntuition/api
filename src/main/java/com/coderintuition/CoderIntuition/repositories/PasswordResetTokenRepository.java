package com.coderintuition.CoderIntuition.repositories;

import com.coderintuition.CoderIntuition.models.PasswordResetToken;
import com.coderintuition.CoderIntuition.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserAndExpiryDateAfterAndInvalidatedFalse(User user, @NotNull Date expiryDate);
}

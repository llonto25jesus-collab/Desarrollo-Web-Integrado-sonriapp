package com.intweb.sonriapp.repository;

import com.intweb.sonriapp.model.TokenPasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenPasswordResetRepository extends JpaRepository<TokenPasswordReset, Integer> {
    Optional<TokenPasswordReset> findByToken(String token);

    @Modifying
    @Query("DELETE FROM TokenPasswordReset t WHERE t.usuario.id = :usuarioId")
    void deleteByUsuarioId(Integer usuarioId);
}
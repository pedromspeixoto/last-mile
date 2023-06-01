package com.lastmile.authservice.repository;

import com.lastmile.authservice.domain.AuthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthClientRepository extends JpaRepository<AuthClientDetails, String> {
    Optional<AuthClientDetails> findByClientId(String clientId);
}

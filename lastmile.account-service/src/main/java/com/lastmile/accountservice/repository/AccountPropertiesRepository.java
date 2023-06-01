package com.lastmile.accountservice.repository;

import com.lastmile.accountservice.domain.AccountProperties;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountPropertiesRepository extends JpaRepository<AccountProperties, String> {

    Optional<AccountProperties> findByEnvironmentAndProperty(String environment, String property);

}
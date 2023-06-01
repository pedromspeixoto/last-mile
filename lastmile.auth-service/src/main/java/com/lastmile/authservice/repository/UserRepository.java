package com.lastmile.authservice.repository;

import com.lastmile.authservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserIdentification(String userIdentification);

    Optional<User> findByUsername(String username);

	void deleteByUsername(String username);

}
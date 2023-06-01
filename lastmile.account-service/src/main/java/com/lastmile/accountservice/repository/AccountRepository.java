package com.lastmile.accountservice.repository;

import com.lastmile.accountservice.domain.Account;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUserIdentification(String userIdentification);

    void deleteById(Long id);

    void deleteByUserIdentification(String userIdentification);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM accounts " +
        "WHERE role LIKE CONCAT('%', UPPER(:role), '%') " +
        "AND COALESCE(UPPER(first_name), '') LIKE CONCAT('%', UPPER(:first_name), '%') " +
        "AND COALESCE(UPPER(last_name), '') LIKE CONCAT('%', UPPER(:last_name), '%') " +
        "AND COALESCE(phone_number, '') LIKE CONCAT('%', :phone_number, '%') " +
        "AND COALESCE(email, '') LIKE CONCAT('%', :email, '%') " +
        "AND account_type LIKE CONCAT('%', UPPER(:account_type), '%')",
    nativeQuery = true
    )
    List<Account> findAllAccounts(@Param("role") String role,
                                  @Param("first_name") String firstName,
                                  @Param("last_name") String lastName,
                                  @Param("phone_number") String phoneNumber,
                                  @Param("email") String email,
                                  @Param("account_type") String accountType,
                                  Pageable pageable);
}
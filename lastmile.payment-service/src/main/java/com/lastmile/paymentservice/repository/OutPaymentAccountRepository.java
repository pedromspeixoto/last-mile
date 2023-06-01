package com.lastmile.paymentservice.repository;

import com.lastmile.paymentservice.domain.OutPaymentAccount;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutPaymentAccountRepository extends JpaRepository<OutPaymentAccount, String> {

    Optional<OutPaymentAccount> findByOutPaymentAccountIdentification(String outPaymentAccountIdentification);

    Optional<OutPaymentAccount> findByAccountBankAccountCountryCodeAndStatus(String accountBankAccountCountryCode, String status);

    Optional<OutPaymentAccount> findByExternalEntityIdentification(String externalEntityIdentification);

    void deleteById(Long id);

    void deleteByOutPaymentAccountIdentification(String outPaymentAccountIdentification);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM out_payments_accounts " +
        "WHERE out_payment_account_identification LIKE CONCAT('%', LOWER(:out_payment_account_identification), '%')",
    nativeQuery = true
    )
    List<OutPaymentAccount> findAllOutPaymentAccounts(@Param("out_payment_account_identification") String outPaymentAccountIdentification,
                                                      Pageable pageable);
}
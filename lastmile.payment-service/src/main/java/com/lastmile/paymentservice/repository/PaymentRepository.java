package com.lastmile.paymentservice.repository;

import com.lastmile.paymentservice.domain.Payment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByPaymentIdentification(String paymentIdentification);

    Optional<Payment> findByExternalPaymentIdentification(String externalPaymentIdentification);

    List<Payment> findByRequesterEntityIdentification(String requesterEntityIdentification);

    List<Payment> findByRequesterEntityIdentificationAndRequesterEntityType(String requesterEntityIdentification, String requesterEntityType);

    void deleteById(Long id);

    void deleteByPaymentIdentification(String paymentIdentification);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM payments " +
        "WHERE requester_entity_identification LIKE CONCAT('%', LOWER(:requester_entity_identification), '%') " +
        "AND requester_entity_type LIKE CONCAT('%', UPPER(:requester_entity_type), '%') " +
        "AND payment_identification LIKE CONCAT('%', LOWER(:payment_identification), '%')",
    nativeQuery = true
    )
    List<Payment> findAllPayments(@Param("requester_entity_identification") String requesterEntityIdentification,
                                  @Param("requester_entity_type") String requesterEntityType,
                                  @Param("payment_identification") String paymentIdentification,
                                  Pageable pageable);
}
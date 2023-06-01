package com.lastmile.paymentservice.repository;

import com.lastmile.paymentservice.domain.OutPayment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutPaymentRepository extends JpaRepository<OutPayment, String> {

    Optional<OutPayment> findByOutPaymentIdentification(String outPaymentIdentification);

    Optional<OutPayment> findByExternalPaymentIdentification(String externalPaymentIdentification);

    List<OutPayment> findByRequesterEntityIdentification(String requesterEntityIdentification);

    List<OutPayment> findByRequesterEntityIdentificationAndRequesterEntityType(String requesterEntityIdentification, String requesterEntityType);

    void deleteById(Long id);

    void deleteByOutPaymentIdentification(String outPaymentIdentification);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM out_payments " +
        "WHERE requester_identification LIKE CONCAT('%', LOWER(:requester_identification), '%') " +
        "AND requester_entity_type LIKE CONCAT('%', UPPER(:requester_entity_type), '%') " +
        "AND out_payment_identification LIKE CONCAT('%', LOWER(:out_payment_identification), '%')",
    nativeQuery = true
    )
    List<OutPayment> findAllOutPayments(@Param("requester_identification") String requesterEntityIdentification,
                                        @Param("requester_entity_type") String requesterEntityType,
                                        @Param("out_payment_identification") String outPaymentIdentification,
                                        Pageable pageable);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM out_payments " +
        "WHERE payment_scheduled_date IS NULL " +
        "AND status = 'PENDING'",
    nativeQuery = true
    )
    List<OutPayment> getPaymentsWithoutScheduledDate();

    @Query(
    value = "" +
        "SELECT * " +
        "FROM out_payments " +
        "WHERE CURRENT_TIMESTAMP > payment_scheduled_date " +
        "AND status IN ('PENDING', 'FAILED')",
    nativeQuery = true
    )
    List<OutPayment> getPaymentsToBeProcessed();
}
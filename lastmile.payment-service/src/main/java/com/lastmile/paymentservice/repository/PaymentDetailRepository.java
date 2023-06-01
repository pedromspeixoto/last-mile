package com.lastmile.paymentservice.repository;

import com.lastmile.paymentservice.domain.PaymentDetail;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, String> {

    Optional<PaymentDetail> findByPaymentDetailIdentification(String paymentDetailIdentification);
    
    Optional<PaymentDetail> findByExternalEntityIdentification(String externalEntityIdentification);

    List<PaymentDetail> findByEntityIdentification(String entityIdentification);

    List<PaymentDetail> findByEntityIdentificationAndEntityType(String entityIdentification, String entityType);

    void deleteById(Long id);

    void deleteByPaymentDetailIdentification(String paymentDetailIdentification);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM payment_details " +
        "WHERE entity_identification LIKE CONCAT('%', LOWER(:entity_identification), '%') " +
        "AND entity_type LIKE CONCAT('%', UPPER(:entity_type), '%') " +
        "AND payment_detail_identification LIKE CONCAT('%', LOWER(:payment_detail_identification), '%')",
    nativeQuery = true
    )
    List<PaymentDetail> findAllPaymentDetails(@Param("entity_identification") String entityIdentification,
                                              @Param("entity_type") String entityType,
                                              @Param("payment_detail_identification") String paymentIdentification,
                                              Pageable pageable);
}
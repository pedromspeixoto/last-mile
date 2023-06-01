package com.lastmile.paymentservice.repository;

import com.lastmile.paymentservice.domain.Invoice;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, String> {

    Optional<Invoice> findByInvoiceIdentification(String invoiceIdentification);

    List<Invoice> findByPaymentIdentification(String paymentIdentification);

    List<Invoice> findByEntityIdentification(String entityIdentification);

    void deleteById(Long id);

    void deleteByInvoiceIdentification(String invoiceIdentification);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM invoices " +
        "WHERE entity_identification LIKE CONCAT('%', LOWER(:entity_identification), '%') " +
        "AND entity_type LIKE CONCAT('%', UPPER(:entity_type), '%') " +
        "AND payment_identification LIKE CONCAT('%', LOWER(:payment_identification), '%') " +
        "AND invoice_identification LIKE CONCAT('%', LOWER(:invoice_identification), '%')",
    nativeQuery = true
    )
    List<Invoice> findAllInvoices(@Param("entity_identification") String entityIdentification,
                                  @Param("entity_type") String entityType,
                                  @Param("payment_identification") String paymentIdentification,
                                  @Param("invoice_identification") String invoiceIdentification,
                                  Pageable pageable);
}
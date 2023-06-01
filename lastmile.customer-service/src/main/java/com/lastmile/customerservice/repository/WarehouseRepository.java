package com.lastmile.customerservice.repository;

import com.lastmile.customerservice.domain.Warehouse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, String> {

    Optional<Warehouse> findByCustomerIdentification(String customerIdentification);

    void deleteById(Long id);

    void deleteByWarehouseIdentification(String warehouseIdentification);

    Optional<Warehouse> findByWarehouseIdentification(String warehouseIdentification);
    
    @Query(
    value = "" +
        "SELECT * " +
        "FROM warehouses " +
        "WHERE status LIKE CONCAT('%', UPPER(:status), '%') " +
        "AND customer_identification LIKE CONCAT('%', UPPER(:customer_identification), '%')",
    nativeQuery = true
    )
    List<Warehouse> findAllWarehouses(@Param("status") String status,
                                      @Param("customer_identification") String customerIdentification,
                                      Pageable pageable);
}
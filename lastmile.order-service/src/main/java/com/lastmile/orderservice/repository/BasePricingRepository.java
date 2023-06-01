package com.lastmile.orderservice.repository;

import com.lastmile.orderservice.domain.BasePricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasePricingRepository extends JpaRepository<BasePricing, String> {

    @Query(
    value = "" +
        "SELECT p.* " +
        "FROM pricing_base_configuration p " +
        "ORDER BY execution_order ASC",
    nativeQuery = true
    )
    List<BasePricing> getBasePricingConfiguration();

}
package com.lastmile.orderservice.repository;

import com.lastmile.orderservice.domain.CustomPricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomPricingRepository extends JpaRepository<CustomPricing, String> {

    void deleteById(Long id);

    void deleteByEntityIdentification(String entityIdentification);

    Optional<CustomPricing> findByEntityIdentification(String entityIdentification);

    @Query(
    value = "" +
        "SELECT p.* " +
        "FROM pricing_custom_configuration p " +
        "WHERE p.entity_identification = :entity_identification " +
        "ORDER BY execution_order ASC",
    nativeQuery = true
    )
    List<CustomPricing> getCustomPricingConfiguration(@Param("entity_identification") String entityIdentification);

}
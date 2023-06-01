package com.lastmile.orderservice.repository;

import com.lastmile.orderservice.domain.OrderCoverage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderCoverageRepository extends JpaRepository<OrderCoverage, String> {
               
    @Query(value = "" +
        "SELECT * " +
        "FROM orders_coverage " +
        "WHERE earth_distance( " +
        "   ll_to_earth(latitude, longitude), " +
        "   ll_to_earth(:latitude, :longitude) " +
        ") < radius",
    nativeQuery = true
    )
    List<OrderCoverage> getOrderCoverage(@Param("latitude") Double latitude,
                                         @Param("longitude") Double longitude);

}
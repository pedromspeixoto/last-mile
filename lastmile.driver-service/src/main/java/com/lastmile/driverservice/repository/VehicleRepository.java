package com.lastmile.driverservice.repository;

import com.lastmile.driverservice.domain.Vehicle;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    List<Vehicle> findByMake(String make);

    List<Vehicle> findByModel(String model);

    List<Vehicle> findByCategory(String category);

    List<Vehicle> findByYear(int year);

    @Query(value = "" +
    "SELECT * " +
    "FROM vehicles " +
    "WHERE UPPER(make) LIKE CONCAT('%', UPPER(:make), '%')" +
    "  AND UPPER(model) LIKE CONCAT('%', UPPER(:model), '%')" +
    "  AND UPPER(category) LIKE CONCAT('%', UPPER(:category), '%')" +
    "  AND CAST(year AS TEXT) LIKE CONCAT('%', UPPER(:year), '%')", nativeQuery = true)
    List<Vehicle> search(String make, String model, String category, String year, Pageable pageable);

}
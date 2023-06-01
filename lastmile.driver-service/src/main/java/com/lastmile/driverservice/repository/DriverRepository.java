package com.lastmile.driverservice.repository;

import com.lastmile.driverservice.domain.Driver;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository <Driver, String> {

    Optional<Driver> findByUserIdentification(String userIdentification);

    void deleteById(Long id);

    void deleteByUserIdentification(String userIdentification);

    Optional<Driver> findByDriverIdentification(String driverIdentification);
    
    List<Driver> findByStatus(String status);

    List<Driver> findByFiscalEntityIdentification(String fiscalEntityIdentification);

	List<Driver> findByStatusIn(List<String> status);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM drivers " +
        "WHERE driver_identification LIKE CONCAT('%', LOWER(:driver_identification), '%') " +
        "AND COALESCE(fiscal_entity_identification, '') LIKE CONCAT('%', LOWER(:fiscal_entity_identification), '%')",
    nativeQuery = true
    )
    List<Driver> findByFiscalEntityPageable(@Param("fiscal_entity_identification") String fiscalEntityIdentification,
                                            @Param("driver_identification") String driverIdentification,
                                            Pageable pageable);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM drivers " +
        "WHERE UPPER(status) LIKE CONCAT('%', UPPER(:status), '%') " +
        "AND LOWER(user_identification) LIKE CONCAT('%', LOWER(:user_identification), '%') " +
        "AND LOWER(driver_identification) LIKE CONCAT('%', LOWER(:driver_identification), '%')",
    nativeQuery = true
    )
    List<Driver> findAllDrivers(@Param("status") String status,
                                @Param("user_identification") String userIdentification,
                                @Param("driver_identification") String driverIdentification,
                                Pageable pageable);

    @Query(value = "" +
        "SELECT * " +
        "FROM drivers " +
        "WHERE earth_distance( " +
        "   ll_to_earth(latitude, longitude), " +
        "   ll_to_earth(:latitude, :longitude) " +
        ") < :radius " +
        "AND status LIKE CONCAT('%', UPPER(:status), '%')" +
        "LIMIT :limit",
    nativeQuery = true)
    List<Driver> findDriversByLocationAndRadius(@Param("latitude") float latitude,
                                                @Param("longitude") float longitude,
                                                @Param("radius") int radius,
                                                @Param("limit") int limit,
                                                @Param("status") String status);

}
package com.lastmile.addressservice.repository;

import com.lastmile.addressservice.domain.Address;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {

    Optional<Address> findByAddressIdentification(String addressIdentification);

    List<Address> findByEntityIdentification(String entityIdentification);

    List<Address> findByEntityIdentificationAndEntityType(String entityIdentification, String entityType);

    void deleteById(Long id);

    void deleteByAddressIdentification(String addressIdentification);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM addresses " +
        "WHERE entity_identification LIKE CONCAT('%', LOWER(:entity_identification), '%') " +
        "AND address_identification LIKE CONCAT('%', LOWER(:address_identification), '%')",
    nativeQuery = true
    )
    List<Address> findAllAddresses(@Param("entity_identification") String entityIdentification,
                                   @Param("address_identification") String addressIdentification,
                                   Pageable pageable);
}
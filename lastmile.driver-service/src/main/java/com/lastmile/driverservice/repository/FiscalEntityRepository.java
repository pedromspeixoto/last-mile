package com.lastmile.driverservice.repository;

import com.lastmile.driverservice.domain.FiscalEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiscalEntityRepository extends JpaRepository<FiscalEntity, String> {

    @Query(
    value = "" +
        "SELECT f.* " +
        "FROM fiscal_entities f LEFT JOIN fiscal_entities_users_link feul " +
        "ON f.fiscal_entity_identification = feul.fiscal_entity_identification " +
        "WHERE f.status LIKE CONCAT('%', UPPER(:status), '%') " +
        "AND COALESCE(feul.user_identification, '') LIKE CONCAT('%', LOWER(:user_identification), '%')",
    nativeQuery = true
    )
    List<FiscalEntity> findAllFiscalEntities(@Param("status") String status, @Param("user_identification") String userIdentification, Pageable pageable);

    Optional<FiscalEntity> findByFiscalEntityIdentification(String fiscalEntityIdentification);

    Optional<FiscalEntity> findByFiscalEntityIdentificationAndStatus(String fiscalEntityIdentification, String status);

    void deleteByFiscalEntityIdentification(String fiscalEntityIdentification);
    
    List<FiscalEntity> findByStatus(String status);

}
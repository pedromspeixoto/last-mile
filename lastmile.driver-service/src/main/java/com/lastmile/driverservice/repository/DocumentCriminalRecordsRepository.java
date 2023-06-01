package com.lastmile.driverservice.repository;

import com.lastmile.driverservice.domain.DocumentCriminalRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentCriminalRecordsRepository extends JpaRepository<DocumentCriminalRecord, String> {

    Optional<DocumentCriminalRecord> findByCriminalRecordIdentification(String criminalRecordIdentification);

    Optional<DocumentCriminalRecord> findByDriverIdentification(String driverIdentification);

}
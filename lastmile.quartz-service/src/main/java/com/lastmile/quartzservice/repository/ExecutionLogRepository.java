package com.lastmile.quartzservice.repository;

import java.util.Optional;

import com.lastmile.quartzservice.domain.ExecutionLog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExecutionLogRepository extends JpaRepository<ExecutionLog, String> {

    Optional<ExecutionLog> findByBatchId(String batchId);

    void deleteById(Long id);

}
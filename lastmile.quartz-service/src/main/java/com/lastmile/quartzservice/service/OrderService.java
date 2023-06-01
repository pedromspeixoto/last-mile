package com.lastmile.quartzservice.service;

import com.lastmile.quartzservice.client.orders.OrdersBridge;
import com.lastmile.quartzservice.domain.ExecutionLog;
import com.lastmile.quartzservice.enums.BatchExecutionStatus;
import com.lastmile.quartzservice.repository.ExecutionLogRepository;
import com.lastmile.utils.constants.Constants;
import com.lastmile.utils.logs.CustomLogging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Configuration
public class OrderService {

    @Value("${spring.profiles.active}")
    private String environment;

    private final OrdersBridge ordersBridge;
    private final ExecutionLogRepository executionLogRepository;
    private final CustomLogging logger;

    public OrderService(final OrdersBridge ordersBridge,
                        final ExecutionLogRepository executionLogRepository,
                        final CustomLogging logger) {
        this.ordersBridge = ordersBridge;
        this.executionLogRepository = executionLogRepository;
        this.logger = logger;
    }

    public void callAssignedOrdersBatch(String batchId) {
        logger.info("batch_name: " + Constants.ASSIGNED_ORDERS_BATCH_NAME + ". batch_id: " + batchId + ". starting execution... ");
        ExecutionLog executionLog = new ExecutionLog(environment, Constants.ASSIGNED_ORDERS_BATCH_NAME, batchId);
        Boolean batchResult = ordersBridge.callAssignedOrdersBatch(batchId);
        if (batchResult) {
            executionLog.setStatus(BatchExecutionStatus.SUCCESS.toString());
        } else {
            executionLog.setStatus(BatchExecutionStatus.FAILED.toString());
        }
        executionLogRepository.save(executionLog);
        logger.info("batch_name: " + Constants.ASSIGNED_ORDERS_BATCH_NAME  + ". batch_id: " + batchId  + ". batch executed with status: " + executionLog.getStatus());
    }

    public void callScheduledOrdersBatch(String batchId) {
        logger.info("batch_name: " + Constants.SCHEDULED_ORDERS_BATCH_NAME + ". batch_id: " + batchId + ". starting execution... ");
        ExecutionLog executionLog = new ExecutionLog(environment, Constants.SCHEDULED_ORDERS_BATCH_NAME, batchId);
        Boolean batchResult = ordersBridge.callScheduledOrdersBatch(batchId);
        if (batchResult) {
            executionLog.setStatus(BatchExecutionStatus.SUCCESS.toString());
        } else {
            executionLog.setStatus(BatchExecutionStatus.FAILED.toString());
        }
        executionLogRepository.save(executionLog);
        logger.info("batch_name: " + Constants.SCHEDULED_ORDERS_BATCH_NAME  + ". batch_id: " + batchId  + ". batch executed with status: " + executionLog.getStatus());
    }

}
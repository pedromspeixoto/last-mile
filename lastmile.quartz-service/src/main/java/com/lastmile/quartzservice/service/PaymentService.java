package com.lastmile.quartzservice.service;

import com.lastmile.quartzservice.client.payments.PaymentBridge;
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
public class PaymentService {

    @Value("${spring.profiles.active}")
    private String environment;

    private final PaymentBridge paymentBridge;
    private final ExecutionLogRepository executionLogRepository;
    private final CustomLogging logger;

    public PaymentService(final PaymentBridge paymentBridge,
                          final ExecutionLogRepository executionLogRepository,
                          final CustomLogging logger) {
        this.paymentBridge = paymentBridge;
        this.executionLogRepository = executionLogRepository;
        this.logger = logger;
    }

    public void callOutboundPaymentsBatch(String batchId) {
        logger.info("batch_name: " + Constants.OUTBOUND_PAYMENTS_BATCH_NAME + ". batch_id: " + batchId + ". starting execution... ");
        ExecutionLog executionLog = new ExecutionLog(environment, Constants.OUTBOUND_PAYMENTS_BATCH_NAME, batchId);
        Boolean batchResult = paymentBridge.callOutboundPaymentsBatch(batchId);
        if (batchResult) {
            executionLog.setStatus(BatchExecutionStatus.SUCCESS.toString());
        } else {
            executionLog.setStatus(BatchExecutionStatus.FAILED.toString());
        }
        executionLogRepository.save(executionLog);
        logger.info("batch_name: " + Constants.OUTBOUND_PAYMENTS_BATCH_NAME  + ". batch_id: " + batchId  + ". batch executed with status: " + executionLog.getStatus());
    }

}
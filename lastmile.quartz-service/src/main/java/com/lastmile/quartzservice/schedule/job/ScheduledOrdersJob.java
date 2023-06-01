package com.lastmile.quartzservice.schedule.job;

import java.util.UUID;

import com.lastmile.quartzservice.service.OrderService;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

@Component
@DisallowConcurrentExecution
public class ScheduledOrdersJob implements Job {

    @Autowired
    private OrderService orderService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        String batchId = UUID.randomUUID().toString();
        orderService.callScheduledOrdersBatch(batchId);
    }
}
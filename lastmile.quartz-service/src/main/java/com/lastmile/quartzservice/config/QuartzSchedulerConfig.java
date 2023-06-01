package com.lastmile.quartzservice.config;

import com.lastmile.quartzservice.schedule.job.AssignedOrdersJob;
import com.lastmile.quartzservice.schedule.job.OutboundPaymentsJob;
import com.lastmile.quartzservice.schedule.job.ScheduledOrdersJob;

import org.springframework.core.io.ClassPathResource;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzSchedulerConfig {

    @Value("${orders.assigned.schedule.cron}")
    private String assignedOrdersCronExpression;

    @Value("${orders.scheduled.schedule.cron}")
    private String scheduledOrdersCronExpression;

    @Value("${payments.outbound.schedule.cron}")
    private String outboundPaymentsCronExpression;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory) throws SchedulerException {
        Scheduler scheduler = factory.getScheduler();

        // assigned orders
        if (scheduler.checkExists(JobKey.jobKey("assignedOrdersJobDetail"))) {
            scheduler.deleteJob(JobKey.jobKey("assignedOrdersJobDetail"));
        }
        JobDetail assignOrdersJobDetail = assignedOrdersJobDetail();
        scheduler.scheduleJob(assignOrdersJobDetail, assignedOrdersTrigger());

        // scheduled orders
        if (scheduler.checkExists(JobKey.jobKey("scheduledOrdersJobDetail"))) {
            scheduler.deleteJob(JobKey.jobKey("scheduledOrdersJobDetail"));
        }
        JobDetail scheduledOrdersJobDetail = scheduledOrdersJobDetail();
        scheduler.scheduleJob(scheduledOrdersJobDetail, scheduledOrdersTrigger());

        // outbound payments
        if (scheduler.checkExists(JobKey.jobKey("outboundPaymentsJobDetail"))) {
            scheduler.deleteJob(JobKey.jobKey("outboundPaymentsJobDetail"));
        }
        JobDetail outboundPaymentsJobDetail = outboundPaymentsJobDetail();
        scheduler.scheduleJob(outboundPaymentsJobDetail, outboundPaymentsTrigger());

        // start scheduler
        AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory = new AutowiringSpringBeanJobFactory();
        autowiringSpringBeanJobFactory.setApplicationContext(applicationContext);
        scheduler.setJobFactory(autowiringSpringBeanJobFactory);
        scheduler.start();
        return scheduler;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) throws IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setDataSource(dataSource);
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    // assigned orders
    public JobDetail assignedOrdersJobDetail() {
        return JobBuilder.newJob()
                         .ofType(AssignedOrdersJob.class)
                         .storeDurably()
                         .withIdentity(JobKey.jobKey("assignedOrdersJobDetail"))
                         .withDescription("Assigned Orders Job Detail")
                         .build();
    }

    public Trigger assignedOrdersTrigger() {
        return TriggerBuilder.newTrigger()
                             .withIdentity(TriggerKey.triggerKey("assignedOrdersTrigger"))
                             .withDescription("Assigned Orders Trigger")
                             .withSchedule(CronScheduleBuilder.cronSchedule(assignedOrdersCronExpression))
                             .build();
    }

    // scheduled orders
    public JobDetail scheduledOrdersJobDetail() {

        return JobBuilder.newJob()
                         .ofType(ScheduledOrdersJob.class)
                         .storeDurably()
                         .withIdentity(JobKey.jobKey("scheduledOrdersJobDetail"))
                         .withDescription("Scheduled Orders Job Detail")
                         .build();
    }

    public Trigger scheduledOrdersTrigger() {
        return TriggerBuilder.newTrigger()
                             .withIdentity(TriggerKey.triggerKey("scheduledOrdersTrigger"))
                             .withDescription("Scheduled Orders Trigger")
                             .withSchedule(CronScheduleBuilder.cronSchedule(scheduledOrdersCronExpression))
                             .build();
    }

    // outbound payments
    public JobDetail outboundPaymentsJobDetail() {

        return JobBuilder.newJob()
                         .ofType(OutboundPaymentsJob.class)
                         .storeDurably()
                         .withIdentity(JobKey.jobKey("outboundPaymentsJobDetail"))
                         .withDescription("Outbound Payments Job Detail")
                         .build();
    }

    public Trigger outboundPaymentsTrigger() {
        return TriggerBuilder.newTrigger()
                             .withIdentity(TriggerKey.triggerKey("outboundPaymentsTrigger"))
                             .withDescription("Outbound Payments Trigger")
                             .withSchedule(CronScheduleBuilder.cronSchedule(outboundPaymentsCronExpression))
                             .build();
    }

}
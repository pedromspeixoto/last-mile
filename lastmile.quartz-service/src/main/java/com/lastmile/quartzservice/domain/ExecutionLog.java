package com.lastmile.quartzservice.domain;

import java.util.Date;

import javax.persistence.*;

@Table(name = "execution_log")
@Entity
public class ExecutionLog {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private Long id;

    @Column(name = "environment")
    private String environment;

    @Column(name = "batch_name")
    private String batchName;

    @Column(name = "batch_id")
    private String batchId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "execution_date")
    private Date executionDate;

    @Column(name = "status")
    private String status;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getBatchName() {
        return this.batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Date getExecutionDate() {
        return this.executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.executionDate = new Date();
    }

    public ExecutionLog() {
    }

    public ExecutionLog(String environment,
                        String batchName,
                        String batchId) {
        this.environment = environment;
        this.batchName = batchName;
        this.batchId = batchId;
    }

}

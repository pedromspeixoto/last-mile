package com.lastmile.orderservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "pricing_custom_configuration")
public class CustomPricing extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_order")
    private Integer order;

    @Column(name = "entity_identification")
    private String entityIdentification;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "fee_name")
    private String feeName;

    @Column(name = "fee_type")
    private String feeType;

    @Column(name = "fee_value")
    private Double feeValue;

    @Column(name = "fee_cap")
    private Double feeCap;

    @Column(name = "reference_column")
    private String referenceColumn;

    @Column(name = "reference_column_value")
    private String referenceColumnValue;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getEntityIdentification() {
        return this.entityIdentification;
    }

    public void setEntityIdentification(String entityIdentification) {
        this.entityIdentification = entityIdentification;
    }

    public String getEntityType() {
        return this.entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getFeeName() {
        return this.feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getFeeType() {
        return this.feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public Double getFeeValue() {
        return this.feeValue;
    }

    public void setFeeValue(Double feeValue) {
        this.feeValue = feeValue;
    }

    public Double getFeeCap() {
        return this.feeCap;
    }

    public void setFeeCap(Double feeCap) {
        this.feeCap = feeCap;
    }

    public String getReferenceColumn() {
        return this.referenceColumn;
    }

    public void setReferenceColumn(String referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

    public String getReferenceColumnValue() {
        return this.referenceColumnValue;
    }

    public void setReferenceColumnValue(String referenceColumnValue) {
        this.referenceColumnValue = referenceColumnValue;
    }

}
package com.lastmile.orderservice.domain;

import javax.persistence.*;

@Entity
@Table(name = "orders_tracking")
public class OrderHistory extends Auditable<String> {

    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_identification")
    private String orderIdentification;

    @Column(name = "assigned_driver")
    private String assignedDriver;

    @Column(name = "order_action")
    private String orderAction;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderAction() {
        return this.orderAction;
    }

    public void setOrderAction(String orderAction) {
        this.orderAction = orderAction;
    }

    public String getOrderIdentification() {
        return this.orderIdentification;
    }

    public void setOrderIdentification(String orderIdentification) {
        this.orderIdentification = orderIdentification;
    }

    public String getAssignedDriver() {
        return this.assignedDriver;
    }

    public void setAssignedDriver(String assignedDriver) {
        this.assignedDriver = assignedDriver;
    }

}
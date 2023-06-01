package com.lastmile.orderservice.repository;

import com.lastmile.orderservice.domain.OrderHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, String> {

    List<OrderHistory> findByOrderIdentification(String orderIdentification);

    Optional<OrderHistory> findByOrderIdentificationAndOrderAction(String orderIdentification, String orderAction);

    @Query(
        value = "" +
            "SELECT * " +
            "FROM orders_tracking " +
            "WHERE order_identification = :order_identification " +
            "AND assigned_driver = :driver_identification " +
            "AND order_action = :order_action " +
            "ORDER BY created_date DESC " +
            "LIMIT 1",
        nativeQuery = true
        )
    Optional<OrderHistory> getLatestOrderEntryByActionAndDriver(@Param("order_identification") String orderIdentification,
                                                                @Param("order_action") String orderAction,
                                                                @Param("driver_identification") String driverIdentification);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM orders_tracking " +
        "WHERE order_identification = :order_identification " +
        "AND assigned_driver LIKE CONCAT('%', LOWER(:driver_identification), '%') " +
        "AND created_date >= :start_date " +
        "AND created_date <= :end_date",
    nativeQuery = true
    )
    List<OrderHistory> getOrderHistoryWithFilters(@Param("order_identification") String orderIdentification,
                                                  @Param("start_date") Date startDate,
                                                  @Param("end_date") Date endDate,
                                                  @Param("driver_identification") String driverIdentification);

}
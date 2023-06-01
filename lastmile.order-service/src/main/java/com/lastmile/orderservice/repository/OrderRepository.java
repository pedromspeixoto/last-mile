package com.lastmile.orderservice.repository;

import com.lastmile.orderservice.domain.Order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Optional<Order> findByOrderIdentification(String orderIdentification);

    Optional<Order> findByShortOrderIdentification(String shortOrderIdentification);

    Optional<Order> findByRequesterPhoneNumberAndShortOrderIdentification(String contactPhoneNumber, String shortOrderIdentification);

    List<Order> findByRequesterIdentification(String requesterIdentification);

    List<Order> findByAssignedDriver(String assignedDriver);

    void deleteById(Long id);

    void deleteByOrderIdentification(String orderIdentification);

    List<Order> findAllByRequesterIdentificationOrAssignedDriver(String requesterIdentification, String assignedDriver);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM orders " +
        "WHERE status LIKE CONCAT('%', UPPER(:status), '%') " +
        "AND requester_identification LIKE CONCAT('%', LOWER(:requester_identification), '%') " +
        "AND COALESCE(assigned_driver, '') LIKE CONCAT('%', LOWER(:assigned_driver), '%') " +
        "AND COALESCE(owner_identification, '') LIKE CONCAT('%', LOWER(:owner_identification), '%') " +
        "AND order_identification LIKE CONCAT('%', LOWER(:order_identification), '%')",
    nativeQuery = true
    )
    List<Order> findAllOrders(@Param("status") String status,
                              @Param("order_identification") String orderIdentification,
                              @Param("requester_identification") String requesterIdentification,
                              @Param("assigned_driver") String driverIdentification,
                              @Param("owner_identification") String ownerIdentification,
                              Pageable pageable);
                          
    @Query(
    value = "" +
        "SELECT * " +
        "FROM orders " +
        "WHERE status IN ('ACCEPTED', 'ASSIGNED', 'IN_TRANSIT') " +
        "AND order_ext_voice_session = :order_ext_voice_session",
    nativeQuery = true
    )
    List<Order> findByActivePhoneNumber(@Param("order_ext_voice_session") String orderExtVoiceSession);
              
    @Query(
    value = "" +
        "SELECT * " +
        "FROM orders " +
        "WHERE status IN ('SCHEDULED', 'PENDING') " +
        "AND EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) > ((EXTRACT(EPOCH FROM (COALESCE(scheduled_date, CURRENT_TIMESTAMP))) - pickup_eta - delivery_eta - :avg_driver_assign_time))",
    nativeQuery = true
    )
    List<Order> getOrdersToBeProcessed(@Param("avg_driver_assign_time") Integer avgDriverAssignTime);

    @Query(
    value = "" +
        "SELECT * " +
        "FROM orders " +
        "WHERE status IN ('ASSIGNED') " +
        "AND EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) > (EXTRACT(EPOCH FROM (last_modified_date)) + :order_timeout_value)",
    nativeQuery = true
    )
    List<Order> getOrdersAssignedAndNotProcessed(@Param("order_timeout_value") Integer orderTimeoutValue);

}
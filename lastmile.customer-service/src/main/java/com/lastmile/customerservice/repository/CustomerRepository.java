package com.lastmile.customerservice.repository;

import com.lastmile.customerservice.domain.Customer;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    void deleteById(Long id);

    void deleteByCustomerIdentification(String customerIdentification);

    Optional<Customer> findByCustomerIdentification(String customerIdentification);

    @Query(
    value = "" +
        "SELECT c.* " +
        "FROM customers c LEFT JOIN customers_users_link cul " +
        "ON c.customer_identification = cul.customer_identification " +
        "WHERE c.status LIKE CONCAT('%', UPPER(:status), '%') " +
        "AND COALESCE(cul.user_identification, '') LIKE CONCAT('%', LOWER(:user_identification), '%')",
    nativeQuery = true
    )
    List<Customer> findAllCustomers(@Param("status") String status,
                                    @Param("user_identification") String userIdentification,
                                    Pageable pageable);

}
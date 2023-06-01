package com.lastmile.orderservice.repository;

import com.lastmile.orderservice.domain.OrderProperties;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderPropertiesRepository extends JpaRepository<OrderProperties, String> {

    Optional<OrderProperties> findByEnvironmentAndProperty(String environment, String property);

}
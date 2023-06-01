package com.lastmile.customerservice.repository;

import com.lastmile.customerservice.domain.CustomerUserLink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerUserLinkRepository extends JpaRepository<CustomerUserLink, String> {

    List<CustomerUserLink> findByUserIdentification(String userIdentification);

    List<CustomerUserLink> findByCustomerIdentification(String customerIdentification);

    Optional<CustomerUserLink> findByUserIdentificationAndCustomerIdentification(String userIdentification, String customerIdentification);

}
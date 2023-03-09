package com.vid.rezal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vid.rezal.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

	boolean existsByEmail(String email);

	Customer findByName(String name);
}


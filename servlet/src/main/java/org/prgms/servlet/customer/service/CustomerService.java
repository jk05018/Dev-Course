package org.prgms.servlet.customer.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.prgms.servlet.customer.model.Customer;

public interface CustomerService {
	void createCustomers(List<Customer> customers);

	Customer createCustomer(String email, String name);

	List<Customer> getAllCustomers();

	Optional<Customer> getCustomer(UUID customerId);
}

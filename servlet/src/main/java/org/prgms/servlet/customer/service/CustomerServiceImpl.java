package org.prgms.servlet.customer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.prgms.servlet.customer.repository.CustomerRepository;
import org.prgms.servlet.customer.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {
	private final CustomerRepository customerRepository;

	public CustomerServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	@Transactional
	public void createCustomers(List<Customer> customers) {
		customers.forEach(customerRepository::insert);

	}

	@Override
	public Customer createCustomer(String email, String name) {
		final Customer customer = new Customer(UUID.randomUUID(), name, email,LocalDateTime.now(), LocalDateTime.now());
		return customerRepository.insert(customer);
	}

	@Override
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public Optional<Customer> getCustomer(UUID customerId) {
		return customerRepository.findById(customerId);
	}
}

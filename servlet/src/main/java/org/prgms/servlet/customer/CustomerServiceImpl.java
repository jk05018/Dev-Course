package org.prgms.servlet.customer;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

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
}

package org.prgms.servlet.customer;

import java.util.List;

public interface CustomerService {
	void createCustomers(List<Customer> customers);
	List<Customer> getAllCustomers();
}

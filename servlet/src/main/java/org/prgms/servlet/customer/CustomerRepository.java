package org.prgms.servlet.customer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

	Customer insert(Customer customer);

	Customer update(Customer customer);

	// 위의 둘을 save로 대체하기도 한다.

	List<Customer> findAll();

	Optional<Customer> findById(UUID customerId);
	Optional<Customer> findByName(String name);
	Optional<Customer> findByEmail(String email);

	void deleteAll();
}

package org.prgms.servlet.customer;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerDto(
	UUID customerId,
	String name,
	String email,
	LocalDateTime lastLoginAt,
	LocalDateTime createdAt) {
	// 필요한 것 전부 집어넣을 수 있고
	// 필요한 것만 집어넣을 수 있다.

	static CustomerDto of(Customer customer) {
		return new CustomerDto(customer.getCustomerId(),
			customer.getName(),
			customer.getEmail(),
			customer.getLastLoginAt(),
			customer.getCreatedAt());
	}

	//
	static Customer to(CustomerDto customerDto) {
		return new Customer(customerDto.customerId(),
			customerDto.name(),
			customerDto.email(),
			customerDto.lastLoginAt(),
			customerDto.createdAt());
	}
}

package org.prgms.servlet.customer.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Customer {
	private final UUID customerId;
	private String name;
	private final String email;
	private LocalDateTime lastLoginAt;
	private final LocalDateTime createdAt;

	public Customer(UUID customerId, String name ,String email, LocalDateTime createdAt) {
		validate(name);
		this.name = name;
		this.customerId = customerId;
		this.email = email;
		this.createdAt = createdAt;
	}

	private void validate(String name) {
		if (name.isBlank()) {
			throw new RuntimeException("Name should not be blank");
		}
	}

	public Customer(UUID customerId, String name, String email, LocalDateTime lastLoginAt,
		LocalDateTime createdAt) {
		validate(name);
		this.customerId = customerId;
		this.name = name;
		this.email = email;
		this.lastLoginAt = lastLoginAt;
		this.createdAt = createdAt;
	}

	public void login(){
		this.lastLoginAt = LocalDateTime.now();
	}

	public UUID getCustomerId() {
		return customerId;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	// settet는 무분별하게 넣을려고 하지 말고 비즈니스적으로 필요할 때 바꿔서 사용하도록 하
	public void changeName(String name) {
		validate(name);
		this.name = name;
	}
}

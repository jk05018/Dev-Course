package org.prgms.kdt.customer;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.zaxxer.hikari.HikariDataSource;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	// per class 하 클래스 당 인스턴스가 만들어진다 per method 메서드 단위로 인스턴
class CustomerJdbcTemplateRepositoryTest {

	@Autowired
	CustomerJdbcTemplateRepository customerJdbcTemplateRepository;
	@Autowired
	DataSource dataSource;

	@Configuration
	@ComponentScan(
		basePackages = {"org.prgms.kdt.customer"}
	)
	static class config {

		@Bean
		public DataSource dataSource() {
			return DataSourceBuilder.create()
				.url("jdbc:mysql://localhost/order_mgmt")
				.username("root")
				.password("tmdgks1817@")
				// HikariDataSource는 기본적으로 10개의 Connection을 채워 넣는다.
				.type(HikariDataSource.class)
				.build();
		}

		@Bean
		public JdbcTemplate jdbcTemplate(DataSource dataSource) {
			return new JdbcTemplate(dataSource);
		}

	}

	Customer newCustomer;

	@BeforeAll
	void setUp() {
		newCustomer = new Customer(UUID.randomUUID(), "test-user", "test-user@gmail.com",
			LocalDateTime.now(), LocalDateTime.now());
		customerJdbcTemplateRepository.deleteAll();
	}

	@Order(1)
	@Test
	void testHikariConnectionPool() {
		assertThat(dataSource.getClass().getName(), is("com.zaxxer.hikari.HikariDataSource"));
	}

	@Order(2)
	@DisplayName("고객을 추가할 수 있다.")
	@Test
	void can_insert_customer_test() {
		customerJdbcTemplateRepository.insert(newCustomer);

		final Optional<Customer> retrievedCustomer = customerJdbcTemplateRepository.findById(newCustomer.getCustomerId());
		assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));// 안의 value, properties들이 같은지 비교
	}

	@Order(3)
	@DisplayName("전체 고객을 조회할 수 있다.")
	@Test
	void testFindAll() {
		final List<Customer> all = customerJdbcTemplateRepository.findAll();
		assertThat(all.isEmpty(), is(false));
	}

	@Order(4)
	@DisplayName("Id를 통해 고객을 조회할 수 있다.")
	@Test
	void testFindAllById() {
		final Optional<Customer> customer = customerJdbcTemplateRepository.findById(newCustomer.getCustomerId());
		assertThat(customer.isEmpty(), is(false));
	}

	@Order(5)
	@DisplayName("name을 통해전체 고객을 조회할 수 있다.")
	@Test
	void testFindAllByName() {
		final Optional<Customer> customer = customerJdbcTemplateRepository.findByName(newCustomer.getName());
		assertThat(customer.isEmpty(), is(false));

		final Optional<Customer> unknown = customerJdbcTemplateRepository.findByName("unknown-user");
		assertThat(unknown.isEmpty(), is(true));
	}

	@Order(6)
	@DisplayName("email을 통해 전체 고객을 조회할 수 있다.")
	@Test
	void testFindAllByEmail() {
		final Optional<Customer> customer = customerJdbcTemplateRepository.findByEmail(newCustomer.getEmail());
		assertThat(customer.isEmpty(), is(false));

		final Optional<Customer> unknown = customerJdbcTemplateRepository.findByEmail("unknown-user@gmail.com");
		assertThat(unknown.isEmpty(), is(true));
	}

	@Order(7)
	@DisplayName("customer을 update 할 수 있다.")
	@Test
	void updateCustomer() {
		newCustomer.changeName("update-customer");

		customerJdbcTemplateRepository.update(newCustomer);

		final Optional<Customer> updatedCustomer = customerJdbcTemplateRepository.findById(newCustomer.getCustomerId());

		assertThat(updatedCustomer.get(), samePropertyValuesAs(newCustomer));

	}

}

package org.prgms.kdt.customer;

import static com.wix.mysql.EmbeddedMysql.*;
import static com.wix.mysql.ScriptResolver.*;
import static com.wix.mysql.config.Charset.*;
import static com.wix.mysql.config.MysqldConfig.*;
import static com.wix.mysql.distribution.Version.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import com.zaxxer.hikari.HikariDataSource;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerServiceTest {

	@Autowired
	CustomerNamedJdbcTemplateRepository customerJdbcTemplateRepository;
	@Autowired
	DataSource dataSource;

	@Configuration
	@ComponentScan(
		basePackages = {"org.prgms.kdt.customer"}
	)
	static class config {

		@Bean
		public DataSource dataSource() {
			// mysql??? ?????? ????????? ??????????????? h2???????????????????????? ?????? sql?????? ???????????? ?????? ???????????? ?????? ??? ??????.
			// mysql embedded??? ??????????????? ??????????????? ?????? ???????????? ????????? ?????????
			// return new EmbeddedDatabaseBuilder()
			// 	.generateUniqueName(true)
			// 	.setType(H2)
			// 	.setScriptEncoding("UTF-8")
			// 	.ignoreFailedDrops(true)
			// 	.addScript("schema.sql")
			// 	.build();
			return DataSourceBuilder.create()
				.url("jdbc:mysql://localhost:2215/test-order_mgmt")
				.username("test")
				.password("test1234!")
				// HikariDataSource??? ??????????????? 10?????? Connection??? ?????? ?????????.
				.type(HikariDataSource.class)
				.build();
		}

		@Bean
		public JdbcTemplate jdbcTemplate(DataSource dataSource) {
			return new JdbcTemplate(dataSource);
		}

		@Bean
		public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
			return new NamedParameterJdbcTemplate(jdbcTemplate);
		}

		@Bean
		public PlatformTransactionManager platformTransactionManager(){
			return new DataSourceTransactionManager(dataSource());
		}

	}

	Customer newCustomer;
	EmbeddedMysql embeddedMysql;

	@BeforeAll
	void setUp() {
		newCustomer = new Customer(UUID.randomUUID(), "test-user", "test-user@gmail.com",
			LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
		MysqldConfig config = aMysqldConfig(v8_0_11)
			.withCharset(UTF8)
			.withPort(2215) // ?????? PORT??? ??? ?????? ??? ??? ?????? ????????? ????????? PORT??? ???
			.withUser("test", "test1234!")
			.withTimeZone("Asia/Seoul")
			.build();
		embeddedMysql = anEmbeddedMysql(config)
			.addSchema("test-order_mgmt", classPathScript("schema.sql"))
			.start();
	}

	@AfterAll
	void cleanUp() {
		embeddedMysql.stop();
	}

	@Order(1)
	@Test
	void testHikariConnectionPool() {
		assertThat(dataSource.getClass().getName(), is("com.zaxxer.hikari.HikariDataSource"));
	}

	@Order(2)
	@DisplayName("????????? ????????? ??? ??????.")
	@Test
	void can_insert_customer_test() {
		customerJdbcTemplateRepository.insert(newCustomer);

		final Optional<Customer> retrievedCustomer = customerJdbcTemplateRepository.findById(
			newCustomer.getCustomerId());
		assertThat(retrievedCustomer.get(), samePropertyValuesAs(newCustomer));// ?????? value, properties?????? ????????? ??????
	}

	@Order(3)
	@DisplayName("?????? ????????? ????????? ??? ??????.")
	@Test
	void testFindAll() {
		final List<Customer> all = customerJdbcTemplateRepository.findAll();
		assertThat(all.isEmpty(), is(false));
	}

	@Order(4)
	@DisplayName("Id??? ?????? ????????? ????????? ??? ??????.")
	@Test
	void testFindAllById() {
		final Optional<Customer> customer = customerJdbcTemplateRepository.findById(newCustomer.getCustomerId());
		assertThat(customer.isEmpty(), is(false));
	}

	@Order(5)
	@DisplayName("name??? ???????????? ????????? ????????? ??? ??????.")
	@Test
	void testFindAllByName() {
		final Optional<Customer> customer = customerJdbcTemplateRepository.findByName(newCustomer.getName());
		assertThat(customer.isEmpty(), is(false));

		final Optional<Customer> unknown = customerJdbcTemplateRepository.findByName("unknown-user");
		assertThat(unknown.isEmpty(), is(true));
	}

	@Order(6)
	@DisplayName("email??? ?????? ?????? ????????? ????????? ??? ??????.")
	@Test
	void testFindAllByEmail() {
		final Optional<Customer> customer = customerJdbcTemplateRepository.findByEmail(newCustomer.getEmail());
		assertThat(customer.isEmpty(), is(false));

		final Optional<Customer> unknown = customerJdbcTemplateRepository.findByEmail("unknown-user@gmail.com");
		assertThat(unknown.isEmpty(), is(true));
	}

	@Order(7)
	@DisplayName("customer??? update ??? ??? ??????.")
	@Test
	void updateCustomer() {
		newCustomer.changeName("update-customer");

		customerJdbcTemplateRepository.update(newCustomer);

		final Optional<Customer> updatedCustomer = customerJdbcTemplateRepository.findById(newCustomer.getCustomerId());

		assertThat(updatedCustomer.get(), samePropertyValuesAs(newCustomer));

	}

	@Order(8)
	@DisplayName("???????????? ?????????.")
	@Disabled // NullPointer ?????? ?????? ????????? ??????
	@Test
	void testTransaction() {
		final Optional<Customer> prevOne = customerJdbcTemplateRepository.findById(newCustomer.getCustomerId());
		assertThat(prevOne.isEmpty(), is(false));
		final Customer newOne = new Customer(UUID.randomUUID(), "a", "a@gmail.com", LocalDateTime.now(), LocalDateTime.now());
		final Customer insertedNewOne = customerJdbcTemplateRepository.insert(newOne);
		customerJdbcTemplateRepository.testTransaction(
			new Customer(insertedNewOne.getCustomerId(), "b", prevOne.get().getEmail(), newOne.getCreatedAt()));

		final Optional<Customer> maybeNewOne = customerJdbcTemplateRepository.findById(insertedNewOne.getCustomerId());
		assertThat(maybeNewOne.isEmpty(), is(false));
		assertThat(maybeNewOne.get(), samePropertyValuesAs(newOne));
	}

}

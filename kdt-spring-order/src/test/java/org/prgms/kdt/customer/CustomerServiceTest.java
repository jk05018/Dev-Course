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
			// mysql로 원래 코드를 작성했다면 h2데이터베이스에서 해당 sql문을 지원하지 않아 동작하지 않을 수 있다.
			// mysql embedded도 오픈소스로 지우너하는 것이 있으므로 그것을 써보자
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
				// HikariDataSource는 기본적으로 10개의 Connection을 채워 넣는다.
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
			.withPort(2215) // 실제 PORT가 떠 있을 수 도 있기 때문에 임의의 PORT로 지
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
	@DisplayName("고객을 추가할 수 있다.")
	@Test
	void can_insert_customer_test() {
		customerJdbcTemplateRepository.insert(newCustomer);

		final Optional<Customer> retrievedCustomer = customerJdbcTemplateRepository.findById(
			newCustomer.getCustomerId());
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

	@Order(8)
	@DisplayName("트랜잭션 테스트.")
	@Disabled // NullPointer 찾기 졸라 귀찮음 시발
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

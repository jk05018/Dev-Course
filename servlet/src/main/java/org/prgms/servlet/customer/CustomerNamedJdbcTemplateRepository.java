package org.prgms.servlet.customer;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Repository
public class CustomerNamedJdbcTemplateRepository implements CustomerRepository {
	private static final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);

	private final NamedParameterJdbcTemplate jdbcTemplate;
	private final DataSourceTransactionManager transactionManager;

	public CustomerNamedJdbcTemplateRepository(NamedParameterJdbcTemplate jdbcTemplate,
		DataSourceTransactionManager transactionManager) {
		this.jdbcTemplate = jdbcTemplate;
		this.transactionManager = transactionManager;
	}

	private Map<String,Object> toParamMap(Customer customer){
		return new HashMap<String,Object>() {{
		put("customerId",customer.getCustomerId().toString().getBytes());
		put("name",customer.getName());
		put("email",customer.getEmail());
		put("lastLoginAt",Timestamp.valueOf(customer.getLastLoginAt()));
		put("createdAt",Timestamp.valueOf(customer.getCreatedAt()));
		}};
	}

	private static final RowMapper<Customer> customerRowMapper = (resultSet, i) -> {
		// 왜 jdbcRepository의 mapToCustomer는 안될까? 람다안에 메서드를 넣는 것을 공부해야 할까?
		final UUID customerId = toUUID(resultSet.getBytes("customer_id"));
		final String name = resultSet.getString("name");
		final String email = resultSet.getString("email");
		final LocalDateTime lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
			resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
		final LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

		return new Customer(customerId, name, email, lastLoginAt, createdAt);
	};

	@Override
	public Customer insert(Customer customer) {

		final int insertedRow = jdbcTemplate.update(
			"INSERT INTO customers(customer_id, name, email, last_login_at, created_at) VALUES (UUID_TO_BIN(:customerId),:name,:email,:lastLoginAt,:createdAt)",
			toParamMap(customer));
		if (insertedRow != 1) {
			throw new RuntimeException("Noting was Inserted");
		}

		return customer;

	}

	@Override
	public Customer update(Customer customer) {
		final int updatedRow = jdbcTemplate.update(
			"UPDATE customers SET  name = :name, email = :email, last_login_at = :lastLoginAt, created_at = :createdAt WHERE customer_id = UUID_TO_BIN(:customerId)",
			toParamMap(customer));
		if (updatedRow != 1) {
			logger.error(MessageFormat.format("customer not updated customer_id : {0}", customer.getCustomerId()));
		}

		return customer;
	}


	public int count() {
		return jdbcTemplate.queryForObject("select count(*) from customers", Collections.emptyMap(), Integer.class);
	}


	@Override
	public List<Customer> findAll() {
		return jdbcTemplate.query("select * from customers", customerRowMapper);
	}

	@Override
	public Optional<Customer> findById(UUID customerId) {
		// 단 한 건의 결과를 가지고 오고 싶을 때 queryForObject
		// queryForObject는 preparedStatement가 만들어진다.
		// empty 이거나 1보다 크다면 nullableSingleResult 메서드에 걸려서 예외가 발생한다.
		// query가 타고 타고 들어가서 Execute(con 획득, 수행, 반납) 호출

		try {
			return Optional.ofNullable(
				jdbcTemplate.queryForObject("select * from customers WHERE customer_id = UUID_TO_BIN(:customerId)",
					Collections.singletonMap("customerId", customerId.toString().getBytes()),
				customerRowMapper));
		} catch (EmptyResultDataAccessException e) {
			logger.error(e.getMessage());
			return Optional.empty();
		}

	}

	@Override
	public Optional<Customer> findByName(String name) {
		try {
			return Optional.ofNullable(
				jdbcTemplate.queryForObject("select * from customers where name = :name",
					Collections.singletonMap("name",name),
					customerRowMapper));
		} catch (EmptyResultDataAccessException e) {
			logger.error("Got Empty result find By name");
			return Optional.empty();
		}
	}

	@Override
	public Optional<Customer> findByEmail(String email) {
		try {
			return Optional.ofNullable(
				jdbcTemplate.queryForObject("select * from customers where email = :email",
					Collections.singletonMap("email",email),
					customerRowMapper));
		} catch (EmptyResultDataAccessException e) {
			logger.error("Got Empty result find by Email");
			return Optional.empty();
		}
	}

	public void testTransaction(Customer customer){
		final TransactionStatus transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			jdbcTemplate.update(
				"UPDATE customers SET  name = :name WHERE customer_id = UUID_TO_BIN(:customerId)",
				toParamMap(customer));
			jdbcTemplate.update(
				"UPDATE customers SET email = :email WHERE customer_id = UUID_TO_BIN(:customerId)",
				toParamMap(customer));
			transactionManager.commit(transaction);
		}catch (DataAccessException e){
			logger.error("Got Error", e);
			transactionManager.rollback(transaction);
		}
	}
	@Override
	public void deleteAll() {
		jdbcTemplate.update("delete from customers", Collections.emptyMap());
	}

	public static UUID toUUID(byte[] customer_ids) throws SQLException {
		final ByteBuffer byteBuffer = ByteBuffer.wrap(customer_ids);
		final UUID customerId = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
		return customerId;
	}

	private Customer mapToCustomer(ResultSet resultSet) throws SQLException {
		final UUID customerId = toUUID(resultSet.getBytes("customer_id"));
		final String name = resultSet.getString("name");
		final String email = resultSet.getString("email");
		final LocalDateTime lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
			resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
		final LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

		return new Customer(customerId, name, email, lastLoginAt, createdAt);
	}
}

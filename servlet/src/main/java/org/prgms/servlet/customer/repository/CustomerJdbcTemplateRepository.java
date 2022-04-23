package org.prgms.servlet.customer.repository;

import java.nio.ByteBuffer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.prgms.servlet.customer.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerJdbcTemplateRepository implements CustomerRepository {
	private static final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);

	private final JdbcTemplate jdbcTemplate;

	public CustomerJdbcTemplateRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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
			"INSERT INTO customers(customer_id, name, email, last_login_at, created_at) VALUES"
				+ "(UUID_TO_BIN(?),?,?,?,?)",
			customer.getCustomerId().toString().getBytes(),
			customer.getName(),
			customer.getEmail(),
			Timestamp.valueOf(customer.getLastLoginAt()),
			Timestamp.valueOf(customer.getCreatedAt()));
		if(insertedRow != 1){
			throw new RuntimeException("Noting was Inserted");
		}

		return customer;

	}

	@Override
	public Customer update(Customer customer) {
		final int updatedRow = jdbcTemplate.update(
			"UPDATE customers SET  name = ?, email = ?, last_login_at = ?, created_at = ? WHERE customer_id = UUID_TO_BIN(?)",
			customer.getName(),
			customer.getEmail(),
			Timestamp.valueOf(customer.getLastLoginAt()),
			Timestamp.valueOf(customer.getCreatedAt()),
			customer.getCustomerId().toString().getBytes());
		if(updatedRow != 1){
			logger.error(MessageFormat.format("customer not updated customer_id : {0}", customer.getCustomerId()));
		}

		return customer;
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
		try{
			return Optional.of(jdbcTemplate.queryForObject("select * from customers WHERE customer_id = UUID_TO_BIN(?)", customerRowMapper,
				customerId.toString().getBytes()));
		}catch (EmptyResultDataAccessException e){
			logger.error(e.getMessage());
			return Optional.empty();
		}

	}

	public int count(){
		return jdbcTemplate.queryForObject("select count(*) from customers", Integer.class);
	}

	@Override
	public Optional<Customer> findByName(String name) {
		try{
			return Optional.of(jdbcTemplate.queryForObject("select * from customers where name = ?", customerRowMapper, name));
		}catch (EmptyResultDataAccessException e){
			logger.error("Got Empty result find By name");
			return Optional.empty();
		}
	}

	@Override
	public Optional<Customer> findByEmail(String email) {
		try{
			return Optional.of(jdbcTemplate.queryForObject("select * from customers where email = ?", customerRowMapper, email));
		}catch (EmptyResultDataAccessException e){
			logger.error("Got Empty result find by Email");
			return Optional.empty();
		}
	}

	@Override
	public void deleteAll() {
		jdbcTemplate.update("delete from customers");
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

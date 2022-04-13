package org.prgms.kdt.customer;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerJdbcRepository implements CustomerRepository {
	private static final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);

	private final DataSource dataSource;

	public CustomerJdbcRepository(DataSource dataSource, JdbcTemplate jdbcTemplate) {
		this.dataSource = dataSource;
	}

	@Override
	public Customer insert(Customer customer) {
		try (
			final Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO customers(customer_id, name, email,last_login_at ,created_at) VALUES (UUID_TO_BIN(?),?,?,?,?)");
		) {
			statement.setBytes(1, customer.getCustomerId().toString().getBytes());
			statement.setString(2, customer.getName());
			statement.setString(3, customer.getEmail());
			statement.setTimestamp(4, Timestamp.valueOf(customer.getLastLoginAt()));
			statement.setTimestamp(5, Timestamp.valueOf(customer.getCreatedAt()));
			final int executeUpdate = statement.executeUpdate();
			if (executeUpdate != 1) {
				throw new RuntimeException("Noting was inserted");
			}
			return customer;

		} catch (SQLException throwable) {
			logger.error("got error while closing connection", throwable);
			throw new RuntimeException(throwable);
		}
	}

	@Override
	public Customer update(Customer customer) {
		try (
			final Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(
				"UPDATE customers SET name = ?, email = ?, last_login_at = ?, created_at = ? WHERE customer_id = UUID_TO_BIN(?)");
		) {
			statement.setString(1, customer.getName());
			statement.setString(2, customer.getEmail());
			statement.setTimestamp(3, Timestamp.valueOf(customer.getLastLoginAt()));
			statement.setTimestamp(4, Timestamp.valueOf(customer.getCreatedAt()));
			statement.setBytes(5, customer.getCustomerId().toString().getBytes());
			final int executeUpdate = statement.executeUpdate();
			if (executeUpdate != 1) {
				throw new RuntimeException("Noting was updated");
			}
			return customer;

		} catch (SQLException throwable) {
			logger.error("got error while closing connection", throwable);
			throw new RuntimeException(throwable);
		}
	}

	@Override
	public List<Customer> findAll() {
		List<Customer> customers = new ArrayList<>();

		try (
			// driver manager로 connection을 생성했을 때는 uri, user, password를 입력해주었다.
			// datasource를 di함으로써 나중에 변경할 수 도 있다. 관심사의 분
			final Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement("select * from customers");
			ResultSet resultSet = statement.executeQuery();
		) {
			while (resultSet.next()) {
				mapToCustomer(customers, resultSet);
			}
		} catch (SQLException throwable) {
			logger.error("got error whiel closing connection", throwable);
			// SQL Exception은 checked Exception -> Runtime Exception으로 바꿔준다?
			throw new RuntimeException(throwable);
		}


		return customers;
	}

	@Override
	public Optional<Customer> findById(UUID customerId) {
		List<Customer> allCustomers = new ArrayList<>();
		String SELECT_BY_ID = "select * from customers WHERE customer_id = UUID_TO_BIN(?)";
		try (
			final Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID);
		) {
			// preparedStatement는 Parameter를 세팅해 준다. 인덱스는 1부
			statement.setBytes(1, customerId.toString().getBytes());
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					mapToCustomer(allCustomers, resultSet);
				}
			}
		} catch (SQLException throwable) {
			logger.error("got error whiel closing connection", throwable);
			throw new RuntimeException(throwable);
		}
		return allCustomers.stream().findFirst();
	}

	@Override
	public Optional<Customer> findByName(String name) {
		List<Customer> allCustomers = new ArrayList<>();
		String SELECT_BY_NAME_SQL = "select * from customers WHERE name = ?";
		try (
			final Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME_SQL);
		) {
			// preparedStatement는 Parameter를 세팅해 준다. 인덱스는 1부
			statement.setString(1, name);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					mapToCustomer(allCustomers, resultSet);
				}
			}
		} catch (SQLException throwable) {
			logger.error("got error whiel closing connection", throwable);
			throw new RuntimeException(throwable);
		}
		return allCustomers.stream().findFirst();
	}

	@Override
	public Optional<Customer> findByEmail(String email) {
		List<Customer> allCustomers = new ArrayList<>();
		String SELECT_BY_EMAIL = "select * from customers WHERE email = ?";
		try (
			final Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(SELECT_BY_EMAIL);
		) {
			// preparedStatement는 Parameter를 세팅해 준다. 인덱스는 1부
			statement.setString(1, email);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					mapToCustomer(allCustomers, resultSet);
				}
			}
		} catch (SQLException throwable) {
			logger.error("got error whiel closing connection", throwable);
			throw new RuntimeException(throwable);
		}
		return allCustomers.stream().findFirst();
	}

	@Override
	public void deleteAll() {
		try (
			final Connection connection = dataSource.getConnection();
			PreparedStatement statement = connection.prepareStatement(
				"DELETE FROM customers");
		) {
			statement.executeUpdate();

		} catch (SQLException throwable) {
			logger.error("got error while closing connection", throwable);
			throw new RuntimeException(throwable);
		}
	}

	public static UUID toUUID(byte[] customer_ids) throws SQLException {
		final ByteBuffer byteBuffer = ByteBuffer.wrap(customer_ids);
		final UUID customerId = new UUID(byteBuffer.getLong(), byteBuffer.getLong());
		return customerId;
	}

	private void mapToCustomer(List<Customer> allCustomers, ResultSet resultSet) throws SQLException {
		final UUID customerId = toUUID(resultSet.getBytes("customer_id"));
		final String name = resultSet.getString("name");
		final String email = resultSet.getString("email");
		final LocalDateTime lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
			resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;
		final LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

		allCustomers.add(new Customer(customerId, name, email, lastLoginAt, createdAt));
	}
}

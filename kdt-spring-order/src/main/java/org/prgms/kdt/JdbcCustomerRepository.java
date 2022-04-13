package org.prgms.kdt;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcCustomerRepository {
	private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
	private final String SELECT_SQL_BY_NAME = "select * from customers WHERE name = ?";
	private final String SELECT_ALL_SQL = "select * from customers";
	private final String INSERT_SQL = " INSERT INTO customers(customer_id, name, email) VALUES (UUID_TO_BIN(?),?,?)";
	private final String UPDATE_BY_ID_SQL = "UPDATE customers SET name = ? WHERE customer_id = UUID_TO_BIN(?)";
	private final String DELETE_ALL_SQL = "DELETE FROM customers";

	// Spring과 연관이 없는 내용 4/12 쌩 자바에서 제공해주는 JDBC 실습

	public List<String> findNamesByStatement(String name) {
		// Connection connection = null;
		// Statement statement = null;
		// ResultSet resultSet = null;
		// 코드상에 갱인 정보가 들어가면 안된다!
		String SELECT_SQL = "select * from customers WHERE name = '%s'".formatted(name);
		List<String> names = new ArrayList<>();

		// Connection은 AutoClosable을 구현하고 있다.
		try (
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root",
				"tmdgks1817@");
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(SELECT_SQL);
		) {
			// connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "tmdgks1817@");
			// statement = connection.createStatement();
			// resultSet = statement.executeQuery("select * from customers");
			while (resultSet.next()) {
				String customerName = resultSet.getString("name");
				UUID customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
				// 지금은 Notnull임이 확인되어서 null체크를 하지 않았었는데 만약 nullable하면 null 체크를 실시해 주어야 한다.
				LocalDateTime createdAt = resultSet.getTimestamp("created_at")
					.toLocalDateTime(); // timeStamp로 받아와서 LocalDateTime으로 사용하는 것이 낫다.
				logger.info("customer UUID -> {} , customer name -> {} createdAt -> {}", customerId, customerName,
					createdAt);
				names.add(customerName);

			}
		} catch (SQLException throwables) {
			logger.error("got error whiel closing connection", throwables);
		}

		return names;
	}

	public List<String> findNamesByPreparedStatement(String name) {
		String SELECT_SQL = "select * from customers WHERE name = ?";
		List<String> names = new ArrayList<>();

		try (
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root",
				"tmdgks1817@");
			PreparedStatement statement = connection.prepareStatement(SELECT_SQL);
		) {
			// preparedStatement는 Parameter를 세팅해 준다. 인덱스는 1부
			statement.setString(1, name);
			logger.info("statement -> {}", statement);
			try(ResultSet resultSet = statement.executeQuery()){
				while (resultSet.next()) {
					String customerName = resultSet.getString("name");
					UUID customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
					LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime(); // timeStamp로 받아와서 LocalDateTime으로 사용하는 것이 낫다.
					logger.info("customer UUID -> {} , customer name -> {} createdAt -> {}", customerId, customerName,
						createdAt);
					names.add(customerName);

				}
			}
		} catch (SQLException throwables) {
			logger.error("got error whiel closing connection", throwables);
		}

		return names;
	}

	public List<UUID> findAllIds() {
		List<UUID> ids = new ArrayList<>();

		try (
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root",
				"tmdgks1817@");
			PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
			ResultSet resultSet = statement.executeQuery();
		) {
			while (resultSet.next()) {
				final UUID customerId = toUUID(resultSet.getBytes("customer_id"));
				ids.add(customerId);
			}
		} catch (SQLException throwables) {
			logger.error("got error whiel closing connection", throwables);
		}

		return ids;
	}

	public static UUID toUUID(byte[] customer_ids) throws SQLException {
		final ByteBuffer byteBuffer = ByteBuffer.wrap(customer_ids);
		final UUID customerId = new UUID(byteBuffer.getLong(),byteBuffer.getLong());
		return customerId;
	}

	public int insertCustomer(UUID customerId, String name, String email) {

		try (
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root",
				"tmdgks1817@");
			PreparedStatement statement = connection.prepareStatement(INSERT_SQL);
		) {
			// preparedStatement는 Parameter를 세팅해 준다. 인덱스는 1부
			statement.setBytes(1, customerId.toString().getBytes());
			statement.setString(2, name);
			statement.setString(3, email);
			return statement.executeUpdate();

		} catch (SQLException throwables) {
			logger.error("got error whiel closing connection", throwables);
		}
		return 0;

	}

	public int updateCustomerName(UUID customerId, String name) {

		try (
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root",
				"tmdgks1817@");
			PreparedStatement statement = connection.prepareStatement(UPDATE_BY_ID_SQL);
		) {
			// preparedStatement는 Parameter를 세팅해 준다. 인덱스는 1부
			statement.setString(1, name);
			statement.setBytes(2, customerId.toString().getBytes());
			return statement.executeUpdate();

		} catch (SQLException throwables) {
			logger.error("got error whiel closing connection", throwables);
		}
		return 0;

	}

	public void deleteAllCustomers(){
		try (
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root",
				"tmdgks1817@");
			PreparedStatement statement = connection.prepareStatement(DELETE_ALL_SQL);
		) {
			statement.executeUpdate();
		} catch (SQLException throwables) {
			logger.error("got error whiel closing connection", throwables);
		}
	}

	public static void main(String[] args) {
		// 이런 방식이 SQL 인젝 -> 만약 sql문을 문자열로 작성했을 때 인젝션 공격에 취약하다. ->
		// preparedStatement를 사용하면 일반적ㅇ로 statement를 사용하면 수행할때마다 쿼리의 문장을 분석하고 컴파일하고 실행 단계를 거치게 된다.
		// prepareState는 한번만 세 과정을 거친 뒤 캐쉬에 담아 계속 사용한다. 미리 statement를 만들어 놓는다. 중간에 바꿀 수 없다. 다이나믹이 아니다. 성능상 이점이 있다 세 단계를 거치지 않으므
		final JdbcCustomerRepository jdbcCustomerRepository = new JdbcCustomerRepository();
		// List<String> names = jdbcCustomerRepository.findNamesByPreparedStatement("tester01");
		// names.forEach(v -> logger.info("Found Name -> {}",v));
		// System.out.println("====================");
		// final List<UUID> allName = jdbcCustomerRepository.findAllIds();
		// allName.forEach(v -> logger.info("Found Name -> {}",v));
		// System.out.println("====================");

		final UUID customerId = UUID.randomUUID();
		jdbcCustomerRepository.deleteAllCustomers();
		logger.info("created customerId -> {}", customerId);
		jdbcCustomerRepository.insertCustomer(customerId, "new-user", "new-user@gmail.com");
		//생뚱맞은 UUID가 반환된다?> UUID 의 version이 다르다.
		jdbcCustomerRepository.findAllIds().forEach( v -> logger.info("FOUND customerId : {}", v));

		// jdbcCustomerRepository.findNamesByPreparedStatement("new-user").forEach(v -> logger.info("Found Name -> {}",v));
		// jdbcCustomerRepository.updateCustomerName(customerId, "user_new");
		// jdbcCustomerRepository.findNamesByPreparedStatement("user_new").forEach(v -> logger.info("Found Name -> {}",v));
		// // jdbcCustomerRepository.deleteAllCustomers();
		// jdbcCustomerRepository.insertCustomer(UUID.randomUUID(), "new-user", "new-user@gmail.com");


	}

}

package org.prgms.kdt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcCustomerRepository {
	private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

	public static void main(String[] args) throws SQLException {

		// Spring과 연관이 없는 내용 4/12 쌩 자바에서 제공해주는 JDBC
		// Connection connection = null;
		// Statement statement = null;
		// ResultSet resultSet = null;
		// 코드상에 갱인 정보가 들어가면 안된다!

		// Connection은 AutoClosable을 구현하고 있다.
		try (
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "tmdgks1817@");
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from customers");
			){
			// connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "tmdgks1817@");
			// statement = connection.createStatement();
			// resultSet = statement.executeQuery("select * from customers");
			while (resultSet.next()) {
				String name = resultSet.getString("name");
				UUID customer_id = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
				logger.info("customer UUID -> {} , customer name -> {}", customer_id, name);

			}
		} catch (SQLException throwables) {
			logger.error("got error whiel closing connection", throwables);
			throw throwables;
		}

	}
}

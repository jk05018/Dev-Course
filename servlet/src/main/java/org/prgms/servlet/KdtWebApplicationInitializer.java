package org.prgms.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zaxxer.hikari.HikariDataSource;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {
	private static final Logger log = LoggerFactory.getLogger(KdtWebApplicationInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		log.info("starting server");
		final AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(AppConfig.class);

		final DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

		final ServletRegistration.Dynamic registration = servletContext.addServlet("test", dispatcherServlet);
		registration.addMapping("/*");
		registration.setLoadOnStartup(1);

	}

	@EnableWebMvc
	@Configuration
	@ComponentScan(basePackages = "org.prgms.servlet.customer")
	@EnableTransactionManagement
	static class AppConfig implements WebMvcConfigurer {

		// @EnableWebMvc로 자동으로 설정되게 해 놓았지만
		// @WebMvcConfigurer를 사용하면 설정할 수 있다./
		@Override
		public void configureViewResolvers(ViewResolverRegistry registry) {
			registry.jsp();

		}

		@Bean
		public DataSource dataSource() {
			final HikariDataSource dataSource = DataSourceBuilder.create()
				.url("jdbc:mysql://localhost/order_mgmt")
				.username("root")
				.password("tmdgks1817@")
				// HikariDataSource는 기본적으로 10개의 Connection을 채워 넣는다.
				.type(HikariDataSource.class)
				.build();

			dataSource.setMaximumPoolSize(1000);
			dataSource.setMinimumIdle(100);

			return dataSource;
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
		public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
			return new DataSourceTransactionManager(dataSource);
		}
	}

}

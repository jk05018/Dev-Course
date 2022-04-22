package org.prgms.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;

import org.prgms.servlet.customer.CustomerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import com.zaxxer.hikari.HikariDataSource;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {
	private static final Logger log = LoggerFactory.getLogger(KdtWebApplicationInitializer.class);

	/**
	 * 실제로 루트 ApplicationContext를 만들고, 서비스, 레포지토리와 관련된 빈을 등록시켜 관리하고
	 * dispatchServlet이 들어가는 WebApplicationContext에는 MVC 관련된 내용만 추가하고 WAS랑 root를 부모로 연결 해 줄것이다.
	 * @param servletContext
	 * @throws ServletException
	 */
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		log.info("starting server");
		final AnnotationConfigWebApplicationContext rootApplicationContext = new AnnotationConfigWebApplicationContext();
		rootApplicationContext.register(RootConfig.class);
		final ContextLoaderListener loaderListener = new ContextLoaderListener(rootApplicationContext);
		servletContext.addListener(loaderListener);

		final AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(ServletConfig.class);
		final DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

		final ServletRegistration.Dynamic registration = servletContext.addServlet("test", dispatcherServlet);
		registration.addMapping("/");
		registration.setLoadOnStartup(-1); // setLoadOnStartUp이 기본적으로 -1 로 설정되어 있다
		// 아무것도 세팅 안한다 -> 로드 안한다.
	}

	// service 와  데이터 접근 관련된 빈들만 등록할 것이기 때문에 Web MVC와 관련된 에너테이션이 필요 없다.
	@Configuration
	@ComponentScan(basePackages = "org.prgms.servlet.customer",
		excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class) // customerController 빼고 나머지 빈들을 등록해야 한다.
	)
	@EnableTransactionManagement
	static class RootConfig {
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

	/*// 하나의 컨테이너 안에 전부 들어가 있었다
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		log.info("starting server");
		final AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.register(AppConfig.class);

		final DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext);

		final ServletRegistration.Dynamic registration = servletContext.addServlet("test", dispatcherServlet);
		registration.addMapping("/");
		registration.setLoadOnStartup(1);
	}*/

	@EnableWebMvc
	@Configuration
	@ComponentScan(basePackages = "org.prgms.servlet.customer",
		includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CustomerController.class),
		useDefaultFilters = false // includeFilter를 패스함에도 불구하고 다른 스테레오 타입으로 등록된 클래스들이 등록되는 것을 방지해준다?
	)
	static class ServletConfig implements WebMvcConfigurer, ApplicationContextAware {

		ApplicationContext applicationContext;

		// @EnableWebMvc로 자동으로 설정되게 해 놓았지만
		// @WebMvcConfigurer를 사용하면 설정할 수 있다./
		@Override
		public void configureViewResolvers(ViewResolverRegistry registry) {
			registry.jsp().viewNames(new String[] {"jsp/*"});
			// jsp는 prefix와 suffixfmf wlwjdgo wnwl dksgdkeh ehlsmsrk?

			final SpringResourceTemplateResolver springResourceTemplateResolver = new SpringResourceTemplateResolver();
			final SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
			springResourceTemplateResolver.setApplicationContext(applicationContext);
			springResourceTemplateResolver.setPrefix("/WEB-INF/");
			springResourceTemplateResolver.setSuffix(".html");
			springTemplateEngine.setTemplateResolver(springResourceTemplateResolver);
			final ThymeleafViewResolver thymeleafViewResolver = new ThymeleafViewResolver();
			thymeleafViewResolver.setTemplateEngine(springTemplateEngine); // template enginteㅇㅡㄹ 추가해줘야한다.
			thymeleafViewResolver.setOrder(1);
			thymeleafViewResolver.setViewNames(new String[] {"views/*"});// views 하위에 있는 데이터는 모두 thymelear를 쓰는 것이다?
			registry.viewResolver(thymeleafViewResolver);
		}

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/resources/**") // 이런 resource에 대해서 요청이 오면 우리
				.addResourceLocations("/resources/")
				.setCachePeriod(60)
				.resourceChain(true)
				.addResolver(new EncodedResourceResolver()); // resource에 대한  location 셋압?
		}

		@Override
		public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
		}
	}

}

package org.prgms.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);

		final ServletRegistration.Dynamic registration = servletContext.addServlet("test", new TestServlet());
		registration.addMapping("/*");
		registration.setLoadOnStartup(1);

	}

	// @EnableWebMvc로 등록하면 스프링MVC가 필요한 빈을 자동으로 등록한?
	@EnableWebMvc
	@Configuration
	@ComponentScan(basePackages = "org.prgms.servlet.customer")
	static class AppConfig {

	}
}

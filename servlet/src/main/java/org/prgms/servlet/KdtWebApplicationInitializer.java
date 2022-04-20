package org.prgms.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;

public class KdtWebApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		final ServletRegistration.Dynamic registration = servletContext.addServlet("test", new TestServlet());
		registration.addMapping("/*");
		registration.setLoadOnStartup(1);

	}

}

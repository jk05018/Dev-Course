package com.prgms.ssmc.configuers;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguer implements WebMvcConfigurer {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		// ㅇㅣ; path로 오면 아 view를 호출하라
		registry.addViewController("/").setViewName("index");
		registry.addViewController("/me").setViewName("me");
		registry.addViewController("/admin").setViewName("admin");
	}

}

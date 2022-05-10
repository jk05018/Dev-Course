package org.prgms.servlet.config;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
			.allowedOrigins("*"); // 전체를 다 허용하겠다.
	}

	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		// default는 jackson? json> 우리가 설정하는 것으로 싹 다 바뀐다.
		//xml로 변환해 보자
		final MarshallingHttpMessageConverter messageConverter = new MarshallingHttpMessageConverter();
		final XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
		messageConverter.setMarshaller(xStreamMarshaller);
		messageConverter.setUnmarshaller(xStreamMarshaller);

		converters.add(0, messageConverter);
	}
	
}

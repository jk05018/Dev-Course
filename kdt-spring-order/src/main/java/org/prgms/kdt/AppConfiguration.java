package org.prgms.kdt;

import org.prgms.kdt.configuration.YamlPropertiesFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 각각의 컴포넌트들을 생성하는 클래스 ?
 *  팩토리 클래스라 생각하면 되나?
 * Configuration Metadata를 가지고 있는 IoC 컨테이너
 * 스프링의 ApplicationContext는 해당 컨테이너에서 받아와서 객체들을 생성하고 구성한다.
 *
 * 스프링 컨테이너 역할?
 */
@Configuration
@ComponentScan(basePackages = {"org.prgms.kdt.order","org.prgms.kdt.voucher", "org.prgms.kdt.configuration"})
// @PropertySource("application.properties")
@PropertySource(value = "application.yaml", factory = YamlPropertiesFactory.class)
@EnableConfigurationProperties // 얘는 SpringBoot에 있는 내용이다.
public class AppConfiguration {

}


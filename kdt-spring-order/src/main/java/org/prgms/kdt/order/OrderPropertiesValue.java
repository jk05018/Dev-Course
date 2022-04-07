package org.prgms.kdt.order;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// @Component
public class OrderPropertiesValue implements InitializingBean {

	// @Value로 아예 값을 지정해 줄 수 있고 저장되어있는 Properties 값을 가져올 수 있다.
	// 만약 값이 없을때 기본값을 지정하려면 ${kdt.versiuon:null입니다!} 이렇게 지정해 주면 된다.
	// ${} 키를 전달해서 가지고
	@Value("${kdt.version}")
	private String version;

	@Value("${kdt.minimum-order-amount}")
	private int minimumOrderAmount;

	@Value("${kdt.support-vendors}")
	private List<String>  supportVendors;

	@Value("${JAVA_HOME}")
	private String javaHome;

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("OrderPropertiesValue InitializingBean start");
		System.out.println(MessageFormat.format("version : {0}", version));
		System.out.println(MessageFormat.format("minimumOrderAmount : {0}", minimumOrderAmount));
		System.out.println(MessageFormat.format("support-vendors : {0}", supportVendors));
		System.out.println(MessageFormat.format("java-home : {0}", javaHome));
	}
}

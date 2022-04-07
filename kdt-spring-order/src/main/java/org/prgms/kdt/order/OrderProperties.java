package org.prgms.kdt.order;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kdt")
public class OrderProperties implements InitializingBean {

	private String version;

	private int minimumOrderAmount;

	private List<String> supportVendors;

	private String description;



	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("OrderProperties yaml start");
		System.out.printf("version : %s%n", version);
		System.out.printf("minOrderAmount : %d\n", minimumOrderAmount);
		System.out.println(MessageFormat.format("support vendors : {0}", supportVendors));
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getMinimumOrderAmount() {
		return minimumOrderAmount;
	}

	public void setMinimumOrderAmount(int minimumOrderAmount) {
		this.minimumOrderAmount = minimumOrderAmount;
	}

	public List<String> getSupportVendors() {
		return supportVendors;
	}

	public void setSupportVendors(List<String> supportVendors) {
		this.supportVendors = supportVendors;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}

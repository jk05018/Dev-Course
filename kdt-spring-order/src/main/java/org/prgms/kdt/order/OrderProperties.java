package org.prgms.kdt.order;

import java.text.MessageFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kdt")
public class OrderProperties implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(OrderProperties.class);

	private String version;

	private int minimumOrderAmount;

	private List<String> supportVendors;

	private String description;



	@Override
	public void afterPropertiesSet() throws Exception {
		logger.debug("OrderProperties yaml start");
		logger.debug("version : %s%n", version);
		logger.debug("minOrderAmount : %d\n", minimumOrderAmount);
		logger.debug(MessageFormat.format("support vendors : {0}", supportVendors));
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

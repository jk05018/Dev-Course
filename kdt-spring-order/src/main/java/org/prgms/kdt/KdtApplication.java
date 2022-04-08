package org.prgms.kdt;

import java.text.MessageFormat;
import java.util.UUID;

import org.prgms.kdt.order.OrderProperties;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.JdbcVoucherRepository;
import org.prgms.kdt.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages =
	{"org.prgms.kdt.order","org.prgms.kdt.voucher", "org.prgms.kdt.configuration"})
public class KdtApplication {

	private static final Logger logger = LoggerFactory.getLogger(KdtApplication.class);

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(KdtApplication.class);
		springApplication.setAdditionalProfiles("dev");
		ConfigurableApplicationContext applicationContext = springApplication.run(args);

		var orderProperties = applicationContext.getBean(OrderProperties.class);
		logger.info("OrderTest start");
		logger.info(MessageFormat.format("version : {0}", orderProperties.getVersion()));
		logger.info(MessageFormat.format("minOrderAmount : {0}", orderProperties.getMinimumOrderAmount()));
		logger.info(MessageFormat.format("support-vendors : {0}", orderProperties.getSupportVendors()));
		logger.info(MessageFormat.format("description : {0}", orderProperties.getDescription()));


		var customerId = UUID.randomUUID();

		var voucherRepository = applicationContext.getBean(VoucherRepository.class);
		var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

		System.out.println(
			MessageFormat.format("is Jdbc Repo -> {0}",voucherRepository instanceof JdbcVoucherRepository));
		System.out.println(MessageFormat.format("is Jdbc Repo -> {0}",voucherRepository.getClass().getCanonicalName()));
	}
}

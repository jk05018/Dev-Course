package org.prgms.kdt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.prgms.kdt.order.OrderItem;
import org.prgms.kdt.order.OrderProperties;
import org.prgms.kdt.order.OrderService;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.JdbcVoucherRepository;
import org.prgms.kdt.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class OrderTester {

	// org.prgms.kdt.OrderTester //WARN
	//org.prgms.kdt.A //WARN
	//org.prgms.kdt.voucher // set 'INFO' 이 하위는 INFO로 처리하게 된다.
	private static final Logger logger = LoggerFactory.getLogger(OrderTester.class);

	public static void main(String[] args) throws IOException {
		var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

		/*var version = environment.getProperty("kdt.version",String.class);
		var minOrderAmount = environment.getProperty("kdt.minimum-order-amount",Integer.class);
		List supportVendors = environment.getProperty("kdt.support-vendors", List.class);
		String description = environment.getProperty("kdt.description");

		logger.info(MessageFormat.format("version : {0}", version));
		logger.info(MessageFormat.format("minOrderAmount : {0}", minOrderAmount));
		logger.info(MessageFormat.format("support-vendors : {0}", supportVendors));
		logger.info(MessageFormat.format("description : {0}", description));*/

		var orderProperties = applicationContext.getBean(OrderProperties.class);
		logger.info("logger name => {}", logger.getName());
		logger.info(MessageFormat.format("version : {0}", orderProperties.getVersion()));
		logger.info(MessageFormat.format("minOrderAmount : {0}", orderProperties.getMinimumOrderAmount()));
		logger.info(MessageFormat.format("support-vendors : {0}", orderProperties.getSupportVendors()));
		logger.info(MessageFormat.format("description : {0}", orderProperties.getDescription()));

		var customerId = UUID.randomUUID();

		var voucherRepository = applicationContext.getBean(VoucherRepository.class);
		var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

		System.out.println(
			MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JdbcVoucherRepository));
		System.out.println(
			MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));
		var orderService = applicationContext.getBean(OrderService.class);

		var order = orderService.createOrder(customerId, new ArrayList<OrderItem>() {{
			add(new OrderItem(UUID.randomUUID(), 100L, 1));
		}}, voucher.getVoucherId());

		applicationContext.close();
		Assert.isTrue(order.totalAmount() == 90L,
			MessageFormat.format("totalAmount {0}is not 90L", order.totalAmount()));
	}
}

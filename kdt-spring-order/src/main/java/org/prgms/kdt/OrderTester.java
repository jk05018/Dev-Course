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
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class OrderTester {
	public static void main(String[] args) throws IOException {
		var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

		/*var version = environment.getProperty("kdt.version",String.class);
		var minOrderAmount = environment.getProperty("kdt.minimum-order-amount",Integer.class);
		List supportVendors = environment.getProperty("kdt.support-vendors", List.class);
		String description = environment.getProperty("kdt.description");
		//
		System.out.println(MessageFormat.format("version : {0}", version));
		System.out.println(MessageFormat.format("minOrderAmount : {0}", minOrderAmount));
		System.out.println(MessageFormat.format("support-vendors : {0}", supportVendors));
		System.out.println(MessageFormat.format("description : {0}", description));*/

		/*var orderProperties = applicationContext.getBean(OrderProperties.class);
		System.out.println("OrderTest start");
		System.out.println(MessageFormat.format("version : {0}", orderProperties.getVersion()));
		System.out.println(MessageFormat.format("minOrderAmount : {0}", orderProperties.getMinimumOrderAmount()));
		System.out.println(MessageFormat.format("support-vendors : {0}", orderProperties.getSupportVendors()));
		System.out.println(MessageFormat.format("description : {0}", orderProperties.getDescription()));*/

		Resource resource = applicationContext.getResource("classpath:application.yaml");
		Resource resource2 = applicationContext.getResource("file:sample.txt");
		Resource resource3 = applicationContext.getResource("https://stackoverflow.com/");
		System.out.println(MessageFormat.format("Resource ->{0}", resource3.getClass().getCanonicalName()));
		File file = resource2.getFile();
		List<String> strings = Files.readAllLines(file.toPath()); // file.toiPath() 파일의 경로 지 개행을 기준으로 짤린다
		System.out.println(strings);
		System.out.println(strings.stream().collect(Collectors.joining("\n")));

		ReadableByteChannel readableByteChannel = Channels.newChannel(resource3.getURI().toURL().openStream());
		BufferedReader bufferedReader = new BufferedReader(
			Channels.newReader(readableByteChannel, StandardCharsets.UTF_8));
		Stream<String> lines = bufferedReader.lines();
		System.out.println(lines.collect(Collectors.joining("\n")));

		var customerId = UUID.randomUUID();

		var voucherRepository = applicationContext.getBean(VoucherRepository.class);
		var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

		System.out.println(MessageFormat.format("is Jdbc Repo -> {0}",voucherRepository instanceof JdbcVoucherRepository));
		System.out.println(MessageFormat.format("is Jdbc Repo -> {0}",voucherRepository.getClass().getCanonicalName()));
		var orderService = applicationContext.getBean(OrderService.class);

		var order = orderService.createOrder(customerId, new ArrayList<OrderItem>() {{
			add(new OrderItem(UUID.randomUUID(), 100L, 1));
		}},voucher.getVoucherId());

		applicationContext.close();
		Assert.isTrue(order.totalAmount() == 90L,
			MessageFormat.format("totalAmount {0}is not 90L", order.totalAmount()));
	}
}

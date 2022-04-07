package org.prgms.kdt;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.prgms.kdt.order.OrderItem;
import org.prgms.kdt.order.OrderProperties;
import org.prgms.kdt.order.OrderService;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.JdbcVoucherRepository;
import org.prgms.kdt.voucher.VoucherRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

public class OrderTester {
	public static void main(String[] args) {
		var applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(AppConfiguration.class);
		var environment = applicationContext.getEnvironment();
		environment.setActiveProfiles("local"); //여러개의 프로파일을 사용할 수 있다.
		applicationContext.refresh(); // profile이 적용될 수 있게 refresh?
		// refresh 하면 default profile이 읽히는데 우리는 dev라는 profile을 지정했으므

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

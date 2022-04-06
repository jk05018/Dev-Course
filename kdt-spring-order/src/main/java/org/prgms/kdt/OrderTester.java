package org.prgms.kdt;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.prgms.kdt.AppConfiguration;
import org.prgms.kdt.order.OrderItem;
import org.prgms.kdt.order.OrderService;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.VoucherRepository;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

public class OrderTester {
	public static void main(String[] args) {
		var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

		var customerId = UUID.randomUUID();

		var voucherRepository1 = applicationContext.getBean(VoucherRepository.class);

		var voucher = voucherRepository1.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));
		var orderService = applicationContext.getBean(OrderService.class);

		var order = orderService.createOrder(customerId, new ArrayList<OrderItem>() {{
			add(new OrderItem(UUID.randomUUID(), 100L, 1));
		}},voucher.getVoucherId());

		applicationContext.close();
		Assert.isTrue(order.totalAmount() == 90L,
			MessageFormat.format("totalAmount {0}is not 90L", order.totalAmount()));
	}
}

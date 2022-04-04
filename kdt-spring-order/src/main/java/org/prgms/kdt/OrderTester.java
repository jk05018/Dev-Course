package org.prgms.kdt;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.util.Assert;

public class OrderTester {
	public static void main(String[] args) {
		var customerId = UUID.randomUUID();
		var orderItems = new ArrayList<OrderItem>(){{
			add(new OrderItem(UUID.randomUUID(), 100L, 1));
		}};
		var percentDiscountVoucher = new PercentDiscountVoucher(UUID.randomUUID(), 10L);
		var fixedDiscountVoucher = new FixedAmountVoucher(UUID.randomUUID(),10L);
		var order = new Order(UUID.randomUUID(), customerId, orderItems, percentDiscountVoucher);

		Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90L", order.totalAmount()));

	}
}

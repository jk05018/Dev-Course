package org.prgms.kdt.order;

import java.util.List;
import java.util.UUID;

import org.prgms.kdt.applicationconfiguration.VersionProvider;
import org.prgms.kdt.voucher.VoucherService;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
	private final VoucherService voucherService;
	private final OrderRepository orderRepository;
	private final VersionProvider versionProvider;

	public OrderService(VoucherService voucherService, OrderRepository orderRepository,
		VersionProvider versionProvider) {
		this.voucherService = voucherService;
		this.orderRepository = orderRepository;
		this.versionProvider = versionProvider;
	}

	public Order createOrder(UUID customerId, List<OrderItem> orderItems){
		var order =  new Order(UUID.randomUUID(), customerId, orderItems);
		orderRepository.insert(order);
		return order;
	}

	public Order createOrder(UUID customerId, List<OrderItem> orderItems, UUID voucherId){
		var voucher = voucherService.getVoucher(voucherId);
		var order =  new Order(UUID.randomUUID(), customerId, orderItems, voucher);
		orderRepository.insert(order);
		voucherService.useVoucher(voucher);
		return order;
	}
}

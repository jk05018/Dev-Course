package org.prgms.kdt.order;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class MemoryOrderRepository implements OrderRepository {
	private final Map<UUID, Order> storage = new ConcurrentHashMap<>();

	@Override
	public Order insert(Order order) {
		storage.put(order.getOrderId(), order);
		return order;
	}
}

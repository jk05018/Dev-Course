package org.prgms.jpa.domain.order;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class OrderRepositoryTest {

	@Autowired
	OrderRepository orderRepository;

	@Test
	void name() {
		Order order = new Order();
		order.setUuid(UUID.randomUUID().toString());
		order.setOrderStatus(OrderStatus.OPENED);
		order.setOrderDatetime(LocalDateTime.now());
		order.setMemo("----");
		order.setCreatedBy("seunghan hwang");
		order.setCratedAt(LocalDateTime.now());

		orderRepository.save(order);

		orderRepository.findAllByOrderStatus(OrderStatus.OPENED);
		orderRepository.findAllByOrderStatusOrderByOrderDateTime(OrderStatus.OPENED);

	}
}

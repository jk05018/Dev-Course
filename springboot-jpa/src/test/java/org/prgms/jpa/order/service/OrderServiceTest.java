package org.prgms.jpa.order.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prgms.jpa.domain.order.OrderRepository;
import org.prgms.jpa.domain.order.OrderStatus;
import org.prgms.jpa.item.dto.ItemDto;
import org.prgms.jpa.item.dto.ItemType;
import org.prgms.jpa.member.dto.MemberDto;
import org.prgms.jpa.order.dto.OrderDto;
import org.prgms.jpa.order.dto.OrderItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderRepository orderRepository;

	String uuid = UUID.randomUUID().toString();

	// @BeforeEach
	void setUp() {
		// 이렇게 반복되는 생성작업은 beforeEach로 빼 줘도 되겠구나나		// Given
		OrderDto orderDto = OrderDto.builder()
			.uuid(uuid)
			.memo("문앞 보관 해주세요.")
			.orderDatetime(LocalDateTime.now())
			.orderStatus(OrderStatus.OPENED)
			.memberDto(
				MemberDto.builder()
					.name("강홍구")
					.nickName("guppy.kang")
					.address("서울시 동작구만 움직이면 쏜다.")
					.age(33)
					.description("---")
					.build()
			)
			.orderItemDtos(List.of(
				OrderItemDto.builder()
					.price(1000)
					.quantity(100)
					.itemDtos(List.of(
						ItemDto.builder()
							.type(ItemType.FOOD)
							.chef("백종원")
							.price(1000)
							.build()
					))
					.build()
			))
			.build();
		// When
		String uuid = orderService.save(orderDto);

		// Then
		log.info("UUID:{}", uuid);
	}

	// @AfterEach
	void tearDown() {
		orderRepository.deleteAll();
	}

	@Test
	void name() {
		OrderDto orderDto = OrderDto.builder()
			.uuid(uuid)
			.memo("문앞 보관 해주세요.")
			.orderDatetime(LocalDateTime.now())
			.orderStatus(OrderStatus.OPENED)
			.memberDto(
				MemberDto.builder()
					.name("강홍구")
					.nickName("guppy.kang")
					.address("서울시 동작구만 움직이면 쏜다.")
					.age(33)
					.description("---")
					.build()
			)
			.orderItemDtos(List.of(
				OrderItemDto.builder()
					.price(1000)
					.quantity(100)
					.itemDtos(List.of(
						ItemDto.builder()
							.type(ItemType.FOOD)
							.chef("백종원")
							.price(1000)
							.build()
					))
					.build()
			))
			.build();
		// When
		String savedUUID = orderService.save(orderDto);

		log.info("{}", savedUUID);
	}

	// @Test
	// void findAll() {
	// 	Page<OrderDto> orders = orderService.findOrders(PageRequest.of(0, 10));
	// 	log.info("{}", orders);
	// }
	//
	// @Test
	// void findOne() throws NotFoundException {
	// 	log.info("uuid:{}", uuid);
	// 	OrderDto one = orderService.findOne(uuid);
	// 	log.info("{}", one);
	// }
}

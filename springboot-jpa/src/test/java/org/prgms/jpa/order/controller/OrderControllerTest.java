package org.prgms.jpa.order.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prgms.jpa.domain.order.OrderStatus;
import org.prgms.jpa.item.dto.ItemDto;
import org.prgms.jpa.item.dto.ItemType;
import org.prgms.jpa.member.dto.MemberDto;
import org.prgms.jpa.order.dto.OrderDto;
import org.prgms.jpa.order.dto.OrderItemDto;
import org.prgms.jpa.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	OrderService orderService;

	@Autowired
	OrderController orderController;

	@Autowired
	ObjectMapper objectMapper;

	String uuid = UUID.randomUUID().toString();

	@BeforeEach
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

	@Test
	void saveCallTest() throws Exception {
		OrderDto orderDto = OrderDto.builder()
			.uuid(UUID.randomUUID().toString())
			.memo("문앞 보관 해주세요.")
			.orderDatetime(LocalDateTime.now())
			.orderStatus(OrderStatus.OPENED)
			.memberDto(
				MemberDto.builder()
					.name("강홍구")
					.nickName("seunghan_hwang")
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
		mockMvc.perform(post("/orders")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(orderDto)))
			.andExpect(status().isOk())
			.andDo(print());
		// Then
	}

	@Test
	void getOne() throws Exception {
		mockMvc.perform(get("/orders/{uuid}", uuid)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());
	}

	//TODO 페이징에 대하여 공부하기
	@Test
	void getAll() throws Exception {
		mockMvc.perform(get("/orders")
				.param("page", String.valueOf(0))
				.param("size", String.valueOf(10))
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print());

	}
}

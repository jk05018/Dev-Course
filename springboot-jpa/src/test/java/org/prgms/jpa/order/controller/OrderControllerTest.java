package org.prgms.jpa.order.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfigureRestDocs
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
			.andDo(print())
			.andDo(document("order.save",
				requestFields(
					fieldWithPath("uuid").type(JsonFieldType.STRING).description("UUID"),
					fieldWithPath("orderDatetime").type(JsonFieldType.STRING).description("orderDatetime"),
					fieldWithPath("orderStatus").type(JsonFieldType.STRING).description("orderStatus"),
					fieldWithPath("memo").type(JsonFieldType.STRING).description("memo"),
					fieldWithPath("memberDto").type(JsonFieldType.OBJECT).description("memberDto"),
					fieldWithPath("memberDto.id").type(JsonFieldType.NULL).description("memberDto.id"),
					fieldWithPath("memberDto.name").type(JsonFieldType.STRING).description("memberDto.name"),
					fieldWithPath("memberDto.nickName").type(JsonFieldType.STRING).description("memberDto.nickName"),
					fieldWithPath("memberDto.age").type(JsonFieldType.NUMBER).description("memberDto.age"),
					fieldWithPath("memberDto.address").type(JsonFieldType.STRING).description("memberDto.address"),
					fieldWithPath("memberDto.description").type(JsonFieldType.STRING).description("memberDto.description"),
					fieldWithPath("orderItemDtos[]").type(JsonFieldType.ARRAY).description("orderItemDtos"),
					fieldWithPath("orderItemDtos[].id").type(JsonFieldType.NULL).description("orderItemDtos.id"),
					fieldWithPath("orderItemDtos[].price").type(JsonFieldType.NUMBER).description("orderItemDtos.price"),
					fieldWithPath("orderItemDtos[].quantity").type(JsonFieldType.NUMBER).description("orderItemDtos.quantity"),
					fieldWithPath("orderItemDtos[].itemDtos[]").type(JsonFieldType.ARRAY).description("orderItemDtos.itemDtos"),
					fieldWithPath("orderItemDtos[].itemDtos[].price").type(JsonFieldType.NUMBER).description("orderItemDtos.itemDtos.price"),
					fieldWithPath("orderItemDtos[].itemDtos[].stockQuantity").type(JsonFieldType.NUMBER).description("orderItemDtos.itemDtos.stockQuantity"),
					fieldWithPath("orderItemDtos[].itemDtos[].type").type(JsonFieldType.STRING).description("orderItemDtos.itemDtos.type"),
					fieldWithPath("orderItemDtos[].itemDtos[].chef").type(JsonFieldType.STRING).description("orderItemDtos.itemDtos.chef")
				),
				responseFields(
					fieldWithPath("statusCode").type(JsonFieldType.NUMBER).description("상태코드"),
					fieldWithPath("data").type(JsonFieldType.STRING).description("데이터"),
					fieldWithPath("serverDateTime").type(JsonFieldType.STRING).description("응답시간")
				)));
		// 만약 문서와 스펙이 다르다면 알려,
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

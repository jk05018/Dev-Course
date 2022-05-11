package org.prgms.jpa.order.service;

import javax.transaction.Transactional;

import org.prgms.jpa.domain.order.Order;
import org.prgms.jpa.domain.order.OrderRepository;
import org.prgms.jpa.order.convertor.OrderConvertor;
import org.prgms.jpa.order.dto.OrderDto;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderConvertor convertor;
	@Transactional // 이것을 통해 tx.begin(), tx.commit()을 해 주지 안항도 된다, 이것을 이용하자
	public String save(OrderDto orderDto){
		// 1. dto -> entityfh qㄹㅗ 변환
		// 2. orderRepository.save(entity)를 통해 영속화
		// 3. result 반환
		final Order order = convertor.convertOrder(orderDto);
		final Order savedOrder = orderRepository.save(order);

		// Entity를 Transaction이 묶여있지 않는 밖까지 끌고 나가는 것은 좋은 습관이 아니다 -> OSIV 개념과 연관
		// 만약 밖까지 나간다면 우리가 예상하지 못한 쿼리가 나갈 수도 있다?
		// 엔터티가 영속 상태이고 그 상태를 변경하면 가능?
		// transaction은 하나의 단위이다 flush랑 다르다.
		// transaction을 거는 순간 autocommit이 false가 되고 하나 하나 쌓이기 시작한다.
		// flush와 영속성 컨텍스트의 관계도 잘생각해야 한다.
		// flush 하면 쓰지지연 저장소가 commit되고, 1차 캐시는 그대로 남아있게 된다.

		// ID 만을 반환함으로써 원치 않는 곳에서 변화를 방지한다.
		return savedOrder.getUuid();

	}

	public void findAll(){

	}

	public void findOne(){

	}
}

package org.prgms.jpa.order.service;

import javax.transaction.Transactional;

import org.prgms.jpa.domain.order.OrderRepository;
import org.prgms.jpa.order.dto.OrderDto;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;

	@Transactional // 이것을 통해 tx.begin(), tx.commit()을 해 주지 안항도 된다, 이것을 이용하자
	public void save(OrderDto orderDto){
		// 1. dto -> entityfh qㄹㅗ 변환
		// 2. orderRepository.save(entity)를 통해 영속화
		// 3. result 반환





	}

	public void findAll(){

	}

	public void findOne(){

	}
}

package org.prgms.jpa.domain.order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,String> {

	List<Order> findAllByOrderStatus(OrderStatus orderStatus);
	List<Order> findAllByOrderStatusOrderByOrderDateTime(OrderStatus orderStatus);
}

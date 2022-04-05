package org.prgms.kdt;

import java.util.Optional;
import java.util.UUID;

/**
 * 각각의 컴포넌트들을 생성하는 클래스 ?
 *  팩토리 클래스라 생각하면 되나?
 *
 * 스프링 컨테이너 역할?
 */
public class OrderContext {
	public VoucherRepository voucherRepository() {
		return new VoucherRepository() {
			@Override
			public Optional<Voucher> findById(UUID voucherId) {
				return Optional.empty();
			}
		};
	}

	public OrderRepository orderRepository() {
		return new OrderRepository() {
			@Override
			public void insert(Order order) {

			}
		};
	}

	public VoucherService voucherService() {
		return new VoucherService(voucherRepository());
	}

	public OrderService orderService() {
		return new OrderService(voucherService(), orderRepository());
	}
}

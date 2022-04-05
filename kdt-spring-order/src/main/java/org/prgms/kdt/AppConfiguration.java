package org.prgms.kdt;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 각각의 컴포넌트들을 생성하는 클래스 ?
 *  팩토리 클래스라 생각하면 되나?
 * Configuration Metadata를 가지고 있는 IoC 컨테이너
 * 스프링의 ApplicationContext는 해당 컨테이너에서 받아와서 객체들을 생성하고 구성한다.
 *
 * 스프링 컨테이너 역할?
 */
@Configuration
public class AppConfiguration {

	@Bean
	public VoucherRepository voucherRepository() {
		return new VoucherRepository() {
			@Override
			public Optional<Voucher> findById(UUID voucherId) {
				return Optional.empty();
			}
		};
	}

	@Bean
	public OrderRepository orderRepository() {
		return new OrderRepository() {
			@Override
			public void insert(Order order) {

			}
		};
	}

	@Bean
	public VoucherService voucherService(VoucherRepository voucherRepository) {
		return new VoucherService(voucherRepository);
	}

	@Bean
	public OrderService orderService(VoucherService voucherService ,OrderRepository orderRepository) {
		return new OrderService(voucherService, orderRepository);
	}
}

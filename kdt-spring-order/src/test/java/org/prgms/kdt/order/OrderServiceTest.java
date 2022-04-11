package org.prgms.kdt.order;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.MemoryVoucherRepository;
import org.prgms.kdt.voucher.Voucher;
import org.prgms.kdt.voucher.VoucherService;

class OrderServiceTest {

	class OrderRepositoryStub implements OrderRepository {

		@Override
		public Order insert(Order order) {
			return null;
		}
	}

	@DisplayName("Order가 생성되어야 한다 (stub)")
	@Test
	void createOrder() {
		// stub 실습
		// 메서드가 실행된 후, 객체의 상태를 확인하여 올바르게 동작했는지를 확ㅇ니하는 검증법
		//given
		MemoryVoucherRepository voucherRepository = new MemoryVoucherRepository();
		Voucher fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
		voucherRepository.insert(fixedAmountVoucher);
		OrderService sut = new OrderService(new VoucherService(voucherRepository), new OrderRepositoryStub());

		//when
		Order order = sut.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)),
			fixedAmountVoucher.getVoucherId());

		//then 실제로 검증
		assertThat(order.totalAmount(), is(100L));
		assertThat(order.getVoucher().isEmpty(), is(false));
		assertThat(order.getVoucher().get().getVoucherId(), is(fixedAmountVoucher.getVoucherId()));
		assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));

	}

	@DisplayName("오더가 생성되어야 한다 (Mock)")
	@Test
	void createOrderByMock() {
		// Mock
		// 메서드의 리턴 값으로 판단할 수 없는 경우 특정 동작을 수행하는지 확ㅇ니하는 검증법 -> 행위 검증 : 행위에 집중해야한다?

		//given
		VoucherService voucherServiceMock = mock(VoucherService.class);
		OrderRepository orderRepositoryMock = mock(OrderRepository.class);
		Voucher fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
		// 이 부분만 동작한다
		// 아직 클래스가 작성하지 않아도 매개변수와 반환값을 예측하고 미치 개발을 진행할 수 있다.
		when(voucherServiceMock.getVoucher(fixedAmountVoucher.getVoucherId())).thenReturn(fixedAmountVoucher);
		OrderService sut = new OrderService(voucherServiceMock, orderRepositoryMock);

		//when

		Order order = sut.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)),
			fixedAmountVoucher.getVoucherId());

		//then
		assertThat(order.totalAmount(), is(100L));
		assertThat(order.getVoucher().isEmpty(), is(false));
		// 내부적으로 fixedAmountVoucher의 getVoucherId가 호출이 되었는가를 확
		// mockito 에서는 inOrder 라는 함수로 특정 메서드들이 순서대로 실시되었는가에 대해 검증할 수 있따
		InOrder inOrder = inOrder(voucherServiceMock);
		inOrder.verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherId());
		inOrder.verify(voucherServiceMock).useVoucher(fixedAmountVoucher);
		verify(orderRepositoryMock).insert(order);


	}
}

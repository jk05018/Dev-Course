package org.prgms.kdt.voucher;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FixedAmountVoucherTest {
	private static final Logger logger = LoggerFactory.getLogger(FixedAmountVoucherTest.class);

	@BeforeAll
	static void beforeAll() {
		logger.info("@beforeAll - 단 한번 실행 ");
	}

	@BeforeEach
	void setUp() {
		logger.info("@BeforeEach - 매 테스트마다 실행");
	}

	@DisplayName("기본적인 assertEquals 테스트")
	@Test
	void nameAssertEquals() {
		// 첫번째 인자 : 기대되어 지는 값
		// 두번째 인자 : 실제
		assertEquals(1, 1);
	}

	@DisplayName("디스카운트 된 금액은 마이너스가 될 수 없다.")
	@Test
	void testMinusDiscountAmount() {
		FixedAmountVoucher sut = new FixedAmountVoucher(UUID.randomUUID(), 1000);
		assertEquals(0, sut.discount(900));
		assertEquals(0, sut.discount(500));

	}

	@DisplayName("할인 금액은 -가 될 수 없다")
	@Test
	void testWithMinus() {
		assertThrows(IllegalArgumentException.class, () -> new FixedAmountVoucher(UUID.randomUUID(), -100));
	}

	@DisplayName("유효한 할인 금액으로만 생성할 수 있다.")
	@Test
	void testVoucherCreation() {
		assertAll("FixedAmountVoucher Creation ", ()  -> {
			assertThrows(IllegalArgumentException.class, () -> new FixedAmountVoucher(UUID.randomUUID(), -0));
			assertThrows(IllegalArgumentException.class, () -> new FixedAmountVoucher(UUID.randomUUID(), -100));
			assertThrows(IllegalArgumentException.class, () -> new FixedAmountVoucher(UUID.randomUUID(), 10000000));

		});
	}
}

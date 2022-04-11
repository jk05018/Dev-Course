package org.prgms.kdt.voucher;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HamcrestAssertionTest {

	@Test
	@DisplayName(" 여러 hamcrest matcher 테스트")
	void hamcrestTest() {
		// Junit assertion
		assertEquals(2, 1 + 1);
		assertNotEquals(1, 1 + 1);

		// hamcrest assertions -> 다양하게 지원
		assertThat(1 + 1, equalTo(2));
		assertThat(1 + 1, is(2));
		assertThat(1 + 1, anyOf(is(1), is(2)));

		assertThat(1 + 1, not(1));
	}

	@DisplayName("컬렉션에 대한 matcher 테스트")
	@Test
	void hamcrestListMatcherTest() {
		// matcher가 돌면서 개별적으로 다 매칭
		List<Integer> prices = List.of(1, 2, 3);
		assertThat(prices, hasSize(3));

		// 내부의 Item들을 순회하면서 테스트 할 수 있다?
		assertThat(prices, everyItem(greaterThan(0)));

		// 순서가 중요하지 않을 때
		assertThat(prices,containsInAnyOrder(2,1,3));

		// 순서가 중요할 때?
		assertThat(prices,contains(1,2,3));
	}
}

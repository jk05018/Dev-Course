package org.prgms.kdt;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.prgms.kdt.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// 실제 SpringTestContext 프레임워크를 사용할 수 있게 해 준다.
@ExtendWith(SpringExtension.class) // 실질적으 Junit과 상호작용해서 testContext가 만들어지게 하는 것은 Spring Extension을 이용해야 한다.
@ContextConfiguration(classes = KdtApplication.class) // 어떤 식으로 ApplicationContext가 만들어져야 하는지만 알려준다
public class KdtSpringContextTest {

	@Autowired
	ApplicationContext applicationContext;

	@DisplayName("appplicationContext가 생성 되어야 한다.")
	@Test
	void testApplicationContext() {
		// autowired만 하고 하면 null이다

		assertThat(applicationContext, notNullValue());
	}

	@DisplayName("VoucherRepository가 빈으로 등록되어 있어야 한다.")
	@Test
	void name() {
		VoucherRepository bean = applicationContext.getBean(VoucherRepository.class);
		assertThat(bean,notNullValue());
	}
}

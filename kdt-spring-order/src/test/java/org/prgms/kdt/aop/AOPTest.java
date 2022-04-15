package org.prgms.kdt.aop;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.UUID;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.prgms.kdt.KdtApplication;
import org.prgms.kdt.voucher.FixedAmountVoucher;
import org.prgms.kdt.voucher.VoucherRepository;
import org.prgms.kdt.voucher.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.zaxxer.hikari.HikariDataSource;

@SpringJUnitConfig
@ActiveProfiles("test")
public class AOPTest {

	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	VoucherRepository voucherRepository;
	@Autowired
	VoucherService voucherService;

	@Configuration
	@ComponentScan(
		basePackages = {"org.prgms.kdt.voucher", "org.prgms.kdt.aop"}
	)
	@EnableAspectJAutoProxy // AOP를 적용 하려면 적용 해야한다?
	static class config {

	}

	@DisplayName("Aop Test")
	@Test
	void testOrderSerivce() {
		final FixedAmountVoucher fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
		voucherRepository.insert(fixedAmountVoucher);

		// voucherService.getVoucher(fixedAmountVoucher.getVoucherId());

		// 빈으로 등록되어지지 않은 VoucherService는 어떻게 될까 -> AOP가 적용되지 않는다.
		// 아마도 해당 Advice 의 PCD를 지정하면 런타임때 지정된 클래스는 Proxy 클래스가 생겨나고
		// 그거를 호출 -> Spring AOP는 등록된 밴 객체에게만 Proxy 객체가 만들어져서 족용 될 수 있다.
		// final VoucherService voucherService = new VoucherService(voucherRepository);
		// voucherService.getVoucher(fixedAmountVoucher.getVoucherId());
	}
}

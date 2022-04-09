package org.prgms.kdt.voucher;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"local", "default"})
public class MemoryVoucherRepository implements VoucherRepository, InitializingBean, DisposableBean {
	// 스레드 상에서도 안전하게 동작하기 위해 Concurrent Hash Map을 사용 하였다.
	private final Map<UUID, Voucher> storage = new ConcurrentHashMap<>();

	@Override
	public Optional<Voucher> findById(UUID voucherId) {
		return Optional.ofNullable(storage.get(voucherId));
	}

	@Override
	public Voucher insert(Voucher voucher) {
		storage.put(voucher.getVoucherId(), voucher);
		return voucher;
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("postConstruct called!");

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("afterPropertiesSet called");
	}

	@PreDestroy
	public void preDestroy() {
		System.out.println("predestroy called");
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("destroy called");
	}
}

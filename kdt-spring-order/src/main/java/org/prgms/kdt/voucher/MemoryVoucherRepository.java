package org.prgms.kdt.voucher;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Primary
@Repository
public class MemoryVoucherRepository implements VoucherRepository {
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
}

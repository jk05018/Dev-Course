package org.prgms.kdt.voucher;

import java.util.Optional;
import java.util.UUID;

public interface VoucherRepository {
	// @Repository는 구현체에 달아줘야 한다.
	Optional<Voucher> findById(UUID voucherId);
	Voucher insert(Voucher voucher);
}

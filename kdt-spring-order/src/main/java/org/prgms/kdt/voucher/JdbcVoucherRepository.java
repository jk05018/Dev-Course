package org.prgms.kdt.voucher;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public class JdbcVoucherRepository implements VoucherRepository{
	@Override
	public Optional<Voucher> findById(UUID voucherId) {
		return Optional.empty();
	}

	@Override
	public Voucher insert(Voucher voucher) {
		return null;
	}
}

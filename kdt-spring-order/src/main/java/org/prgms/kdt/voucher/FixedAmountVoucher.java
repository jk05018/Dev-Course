package org.prgms.kdt.voucher;

import java.util.UUID;

public class FixedAmountVoucher implements Voucher {
	private final UUID voucherId;
	private final long amount;

	public FixedAmountVoucher(UUID voucherId, long amount) {
		if(amount <= 0 || amount >= 10000000)
			throw new IllegalArgumentException();
		this.voucherId = voucherId;
		this.amount = amount;
	}

	@Override
	public UUID getVoucherId() {
		return voucherId;
	}

	public long discount(long beforeDiscount) {
		long afterDiscount = beforeDiscount - amount;

		if(afterDiscount < 0) return 0L;
		return afterDiscount;
	}

	@Override
	public String toString() {
		return "FixedAmountVoucher{" +
			"voucherId=" + voucherId +
			", amount=" + amount +
			'}';
	}
}

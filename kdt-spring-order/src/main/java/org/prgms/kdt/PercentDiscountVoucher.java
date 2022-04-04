package org.prgms.kdt;

import java.util.UUID;

public class PercentDiscountVoucher implements Voucher{
	private final UUID voucherOd;
	private final long percent;

	public PercentDiscountVoucher(UUID voucherOd, long percent) {
		this.voucherOd = voucherOd;
		this.percent = percent;
	}

	@Override
	public UUID getVoucherId() {
		return voucherOd;
	}

	@Override
	public long discount(long beforeDiscount) {
		return beforeDiscount - beforeDiscount * percent/100;
	}
}

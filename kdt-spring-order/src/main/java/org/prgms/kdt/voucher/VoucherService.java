package org.prgms.kdt.voucher;

import java.text.MessageFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class VoucherService {

	@Autowired
	private final VoucherRepository voucherRepository;

	public VoucherService(VoucherRepository voucherRepository) {
		this.voucherRepository = voucherRepository;
	}

	public Voucher getVoucher(UUID voucherId) {
		return voucherRepository.findById(voucherId)
			.orElseThrow(() -> new RuntimeException(
				MessageFormat.format("Can not find Voucher for id : {0}", voucherId)));
	}

	public void useVoucher(Voucher voucher) {

	}
}

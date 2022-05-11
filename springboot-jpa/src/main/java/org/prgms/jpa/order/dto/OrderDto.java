package org.prgms.jpa.order.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.prgms.jpa.domain.order.OrderStatus;
import org.prgms.jpa.member.dto.MemberDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
	private String uuid;

	private OrderStatus orderStatus;

	private LocalDateTime orderDatetime	;

	private String memo;

	private MemberDto memberDto;
	private List<OrderItemDto> orderItemDtos;
}

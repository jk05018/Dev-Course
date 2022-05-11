package org.prgms.jpa.domain.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order extends BaseEntity {

	@Id
	@Column(name = "id")
	private String uuid;


	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;

	@Column(name = "order_datetime", columnDefinition = "TIMESTAMP")
	private LocalDateTime orderDatetime;

	@Lob
	private String memo;

	@Column(name = "member_id" , insertable = false, updatable = false)
	private Long memberId;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "member_id", referencedColumnName = "id")
	// JoinColumn에 따로 명시하지 않을 경우 default로 우리가 명시한 필드명 + under_bar(_) + pk값을 따라간다 예) member_id
	private Member member;


	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();

	// 양방향 관계에서는 연관관계 편의 메서드를 제공한다.
	// 이거 잘 이해해보
	public void setMember(Member member){
		if(Objects.nonNull(member)){
			member.getOrders().remove(this);
		}

		this.member = member;
		member.getOrders().add(this);
	}

	public void addOrderItem(OrderItem orderItem){
		orderItem.setOrder(this);
	}

}

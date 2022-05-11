package org.prgms.jpa.domain.order;

import java.util.Objects;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.prgms.jpa.order.dto.OrderItemDto;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
@Table(name = "item")
public abstract class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private int price;
	private int stockQuantity;

	@ManyToOne
	@JoinColumn(name = "order_item_id", referencedColumnName = "id")
	private OrderItem orderItem;

	// 연관관계 편의 메서드 START
	public void setOrderItem(OrderItem orderItem) {
		if (Objects.nonNull(this.orderItem)) {
			this.orderItem.getItems().remove(this);
		}

		this.orderItem = orderItem;
		orderItem.getItems().add(this);
	}

	// 연관관계 편의 메서드 END

}

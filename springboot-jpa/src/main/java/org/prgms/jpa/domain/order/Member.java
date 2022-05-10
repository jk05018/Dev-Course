package org.prgms.jpa.domain.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "member")
public class Member {

	@Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "name", nullable = false, length = 30)
	private String name;

	@Column(name = "nick_name", nullable = false , length = 30, unique = true)
	private String nickName;

	@Column(name = "age", nullable = false)
	private int age;

	@Column(name = "address" ,nullable = false)
	private String address;

	@Column(name = "description")
	private String description;

	@OneToMany(mappedBy = "member") // 필드 값을 넣어
	private List<Order> orders = new ArrayList<>();

	public void addOrder(Order order){
		order.setMember(this);
	}


}

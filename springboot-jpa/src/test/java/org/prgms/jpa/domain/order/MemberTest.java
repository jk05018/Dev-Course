package org.prgms.jpa.domain.order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class MemberTest {

	@Autowired
	EntityManagerFactory emf;

	@Test
	void member_insert() {
		Member member = new Member();
		member.setName("hwangseunghan");
		member.setAddress("부산광역시 금정구 장전동");
		member.setAge(25);
		member.setNickName("hani");
		member.setDescription("주니어 백엔드 개발자");

		final EntityManager em = emf.createEntityManager();
		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		em.persist(member);

		tx.commit();
	}

	@Test
	void 연관관계_테스트() {
		final EntityManager em = emf.createEntityManager();
		final EntityTransaction tx = em.getTransaction();
		tx.begin();

		Member member = new Member();
		member.setName("hwangseunghan");
		member.setAddress("부산광역시 금정구 장전동");
		member.setAge(25);
		member.setNickName("hani");

		em.persist(member);

		Order order = new Order();
		order.setUuid(UUID.randomUUID().toString());
		order.setOrderStatus(OrderStatus.OPENED);
		order.setOrderDatetime(LocalDateTime.now());
		order.setMemo("부재시 연락주세요.");
		order.setMember(member);
		member.setOrders(List.of(order));

		em.persist(order);
		tx.commit();

		em.clear();

		final Order findOrder = em.find(Order.class, order.getUuid());

		log.info("{}", findOrder.getMember().getNickName()); //lazy intialize 됨
		log.info("{}", findOrder.getMember().getOrders().size());
	}
}

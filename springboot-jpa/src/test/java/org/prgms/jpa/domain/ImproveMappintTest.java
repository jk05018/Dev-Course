package org.prgms.jpa.domain;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.Test;
import org.prgms.jpa.domain.order.Food;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class ImproveMappintTest {

	@Autowired
	private EntityManagerFactory emf;

	@Test
	void inheritance_test() {
		final EntityManager entityManager = emf.createEntityManager();
		final EntityTransaction tx = entityManager.getTransaction();

		tx.begin();

		Food food = new Food();
		food.setPrice(1000);

		food.setStockQuantity(100);
		food.setChef("백종원");
		entityManager.persist(food);
		tx.commit();
	}
}

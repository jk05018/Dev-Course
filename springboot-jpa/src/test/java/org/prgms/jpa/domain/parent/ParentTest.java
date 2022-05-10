package org.prgms.jpa.domain.parent;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class ParentTest {

	@Autowired
	EntityManagerFactory emf;

	@Test
	void 복합키_테스트() {
		final EntityManager em = emf.createEntityManager();
		final EntityTransaction tx = em.getTransaction();

		tx.begin();

		Parent parent = new Parent();
		parent.setId1("id1");
		parent.setId2("id2");
		em.persist(parent);

		tx.commit();

		final Parent findParent = em.find(Parent.class, new ParentId("id1", "id2"));
		log.info("{}, {}", findParent.getId1(), findParent.getId2());

	}
}

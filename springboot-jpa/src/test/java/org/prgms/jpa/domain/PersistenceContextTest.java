package org.prgms.jpa.domain;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class PersistenceContextTest {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	EntityManagerFactory emf;

	@BeforeEach
	void setUp() {
		customerRepository.deleteAll();
	}

	@Test
	void 저장() {
		final EntityManager entityManager = emf.createEntityManager();
		final EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();
		Customer customer = new Customer(); // 비영속 상태
		customer.setId(1L);
		customer.setFirstName("seugnhan");
		customer.setLastName("hwang");

		entityManager.persist(customer);
		transaction.commit(); // entityManager.flush가 날라 감

	}

	@Test
	void 조회_DB조회() {
		final EntityManager entityManager = emf.createEntityManager();
		final EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();
		Customer customer = new Customer(); // 비영속 상태
		customer.setId(1L);
		customer.setFirstName("seugnhan");
		customer.setLastName("hwang");

		entityManager.persist(customer);
		transaction.commit(); // entityManager.flush가 날라 감

		entityManager.clear();

		final Customer findCustomer = entityManager.find(Customer.class, 1L);
		log.info("{} {} {}", findCustomer.getId(), findCustomer.getFirstName(), findCustomer.getLastName());

	}

	@Test
	void 조회_1차캐시조회() {
		final EntityManager entityManager = emf.createEntityManager();
		final EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();
		Customer customer = new Customer(); // 비영속 상태
		customer.setId(1L);
		customer.setFirstName("seugnhan");
		customer.setLastName("hwang");

		entityManager.persist(customer);
		transaction.commit(); // entityManager.flush가 날라 감

		final Customer findCustomer = entityManager.find(Customer.class, 1L);
		log.info("{} {} {}", findCustomer.getId(), findCustomer.getFirstName(), findCustomer.getLastName());

	}

	@Test
	void 수정() {
		final EntityManager entityManager = emf.createEntityManager();
		final EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();
		Customer customer = new Customer(); // 비영속 상태
		customer.setId(1L);
		customer.setFirstName("seugnhan");
		customer.setLastName("hwang");

		entityManager.persist(customer);
		transaction.commit(); // entityManager.flush가 날라 감

		transaction.begin();
		final Customer findCustomer = entityManager.find(Customer.class, 1L);

		findCustomer.setFirstName("updatedFirstName");
		findCustomer.setLastName("updatedLastName");

		transaction.commit();
		log.info("{} {} {}", findCustomer.getId(), findCustomer.getFirstName(), findCustomer.getLastName());

	}

	@Test
	void 삭제() {
		final EntityManager entityManager = emf.createEntityManager();
		final EntityTransaction transaction = entityManager.getTransaction();

		transaction.begin();
		Customer customer = new Customer(); // 비영속 상태
		customer.setId(1L);
		customer.setFirstName("seugnhan");
		customer.setLastName("hwang");

		entityManager.persist(customer);
		transaction.commit(); // entityManager.flush가 날라 감

		// 영속성 컨텍스트 내에서 삭제 되었기 때문에 더티 체킹으로 인해 DB에 반영된다.
		transaction.begin();
		entityManager.remove(customer);
		transaction.commit();

	}
}

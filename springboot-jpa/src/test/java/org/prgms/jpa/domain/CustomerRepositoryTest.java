package org.prgms.jpa.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@Transactional
class CustomerRepositoryTest {

	@Autowired
	EntityManagerFactory emf;

	@Autowired
	CustomerRepository repository;

	@DisplayName("customer를 저장할 수 있다.")
	@Test
	void save_customer_test(){
		// given
		Customer customer = new Customer();
		customer.setId(1L);
		customer.setFirstName("seunghan");
		customer.setLastName("hwang");
		//when
		repository.save(customer);

		//then
		Customer findCustomer = repository.findById(1L).get();
		log.info("{} {}", findCustomer.getFirstName(), findCustomer.getLastName());

	}

	@DisplayName("customer를 수정할 수 있다.")
	@Test
	void update_customer_test(){
		// given
		Customer customer = new Customer();
		customer.setId(1L);
		customer.setFirstName("seunghan");
		customer.setLastName("hwang");
		Customer findCustomer = repository.save(customer);

		//when
		findCustomer.setFirstName("updatedSeunghan");
		findCustomer.setLastName("updatedHwang");

		//then

		Customer updatedCustomer = repository.findById(1L).get();
		assertThat(updatedCustomer.getFirstName()).isEqualTo("updatedSeunghan");
		assertThat(updatedCustomer.getLastName()).isEqualTo("updatedHwang");
	}

	@DisplayName("customer를 리스트 조회 할 수 있다")
	@Test
	void customer_list_test() {
		// Given
		Customer customer1 = new Customer();
		customer1.setId(1L);
		customer1.setFirstName("jihoon");
		customer1.setLastName("lee");

		Customer customer2 = new Customer();
		customer2.setId(2L);
		customer2.setFirstName("sanghuck");
		customer2.setLastName("Lee");



		repository.saveAll(List.of(customer1, customer2));

		// When
		List<Customer> selectedCustomers = repository.findAll();

		// Then
		assertThat(selectedCustomers.size()).isEqualTo(2);
	}

	@DisplayName("1차 캐시 조회")
	@Test
	void test(){
		// given
		final EntityManager em = emf.createEntityManager();
		final EntityTransaction transaction = em.getTransaction();
		transaction.begin();

		Customer customer = new Customer();
		customer.setId(1L);
		customer.setFirstName("jihoon");
		customer.setLastName("lee");

		em.persist(customer);
		transaction.commit();
		//when
		// 1차 캐시에서 나오기 때문에 조회 쿼리가 날라가지 않을 것이다. -> 확인
		final Customer findCustomer = em.find(Customer.class, 1L);

		//then


	}
}

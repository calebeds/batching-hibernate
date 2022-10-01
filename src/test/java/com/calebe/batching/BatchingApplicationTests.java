package com.calebe.batching;

import com.calebe.batching.domain.Actor;
import com.calebe.batching.domain.Product;
import com.calebe.batching.support.TransactionRunner;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.TypedQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class BatchingApplicationTests {

	@Autowired
	private TransactionRunner txRunner;

	@Test
	void testNativeJDBCBatching() {
		txRunner.executeInTransaction(entityManager -> {
			try {
				Connection connection = entityManager.unwrap(SessionImplementor.class).getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
				PreparedStatement stm = connection.prepareStatement("INSERT INTO products (id, name) VALUES (?, ?)");
				stm.setObject(1, UUID.randomUUID());
				stm.setString(2, "p1");
				stm.addBatch();
				stm.setObject(1, UUID.randomUUID());
				stm.setString(2, "p2");
				stm.addBatch();
				stm.setObject(1, UUID.randomUUID());
				stm.setString(2, "p3");
				stm.addBatch();
				stm.setObject(1, UUID.randomUUID());
				stm.setString(2, "p4");
				stm.addBatch();
				stm.executeBatch();


			} catch (Exception e) {
				System.out.println(e);
			}
		});

	}

	@Test
	void testJDBCBatching() {
		txRunner.executeInTransaction(entityManager -> {
			Product p1 = new Product("p1");
			Product p2 = new Product("p2");
			Product p3 = new Product("p3");
			Product p4 = new Product("p4");
			Product p5 = new Product("p5");
			entityManager.persist(p1);
			entityManager.persist(p2);
			entityManager.persist(p3);
			entityManager.persist(p4);
			entityManager.persist(p5);
		});
	}
	@Test
	void testJDBCBatchingWithFlushing() {
		txRunner.executeInTransaction(entityManager -> {
			Product p1 = new Product("p1");
			Product p2 = new Product("p2");
			Product p3 = new Product("p3");
			Product p4 = new Product("p4");
			Product p5 = new Product("p5");
			entityManager.persist(p1);
			entityManager.persist(p2);
			entityManager.flush();
			entityManager.persist(p3);
			entityManager.persist(p4);
			entityManager.persist(p5);
		});
	}

	@Test
	void testSessionJDBCBatching() {
		txRunner.executeInTransaction(entityManager -> {
			entityManager.unwrap(Session.class).setJdbcBatchSize(3);
			Product p1 = new Product("p1");
			Product p2 = new Product("p2");
			Product p3 = new Product("p3");
			Product p4 = new Product("p4");
			Product p5 = new Product("p5");
			entityManager.persist(p1);
			entityManager.persist(p2);
			entityManager.persist(p3);
			entityManager.persist(p4);
			entityManager.persist(p5);
		});
	}

	@Test
	void testVersionedJDBCBatching() {
		txRunner.executeInTransaction(entityManager -> {
			Actor a1 = new Actor(1, "PENELOPE");
			Actor a2 = new Actor(2, "NICK");
			Actor a3 = new Actor(3, "ED");
			Actor a4 = new Actor(4, "JENNIFER");
			Actor a5 = new Actor(5, "JOHNNY");
			entityManager.persist(a1);
			entityManager.persist(a2);
			entityManager.persist(a3);
			entityManager.persist(a4);
			entityManager.persist(a5);
		});

		txRunner.executeInTransaction(entityManager -> {
			TypedQuery<Actor> query = entityManager.createQuery("FROM Actor a", Actor.class);
			List<Actor> actors = query.getResultList();
			actors.forEach(a -> {
				a.setFirstName(a.getFirstName() + "X");
			});
		});

	}


}

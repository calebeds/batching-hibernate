package com.calebe.batching;

import com.calebe.batching.domain.Product;
import com.calebe.batching.support.TransactionRunner;
import org.hibernate.engine.spi.SessionImplementor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
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



}

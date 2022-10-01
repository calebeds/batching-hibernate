package com.calebe.batching;

import com.calebe.batching.domain.Product;
import com.calebe.batching.domain.Review;
import com.calebe.batching.support.TransactionRunner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.TypedQuery;
import java.lang.reflect.Type;
import java.util.List;

@SpringBootTest
@ActiveProfiles("order")
public class OrderedBatchingApplicationTests {
    @Autowired
    private TransactionRunner txRunner;

    @Test
    void testOrderedInsertJDBCBatching() {
        txRunner.executeInTransaction(entityManager -> {
            Product p1 = new Product("p1");
            p1.addReview(new Review(1));
            Product p2 = new Product("p2");
            p2.addReview(new Review(2));
            Product p3 = new Product("p3");
            p3.addReview(new Review(3));
            entityManager.persist(p1);
            entityManager.persist(p2);
            entityManager.persist(p3);
        });
    }

    @Test
    void testOrderedUpdateJDBCBatching() {
        txRunner.executeInTransaction(entityManager -> {
            Product p1 = new Product("p1");
            p1.addReview(new Review(1));
            Product p2 = new Product("p2");
            p2.addReview(new Review(2));
            Product p3 = new Product("p3");
            p3.addReview(new Review(3));

            txRunner.executeInTransaction(entityManager1 -> {
                TypedQuery<Product> query = entityManager.createQuery("FROM Product p JOIN FETCH p.reviews", Product.class);
                List<Product> products = query.getResultList();
                products.forEach(p -> {
                    p.setName(p.getName() + "x");
                    p.getReviews().forEach(r -> {
                        r.setValue(r.getValue() + 1);
                    });
                });
            });

            entityManager.persist(p1);
            entityManager.persist(p2);
            entityManager.persist(p3);
        });
    }
}

package com.calebe.batching.domain;

import javax.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue
    private int id;
    private int value;

    @ManyToOne
    private Product product;

    public Review(int value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}

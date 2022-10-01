package com.calebe.batching.domain;

import javax.persistence.*;

@Entity
@Table(name = "actors")
public class Actor {
    @Id
    @Column(name = "actor_id")
    private int id;
    @Column(name = "first_name")
    private String firstName;

    @Version
    private int version;

    public Actor() {
    }

    public Actor(int id, String firstName) {
        this.id = id;
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}

package com.ing.brokerage.auth.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    public Role(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Role() {
    }
}
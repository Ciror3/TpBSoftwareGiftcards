package com.example.giftcards.giftcards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table (name= "merchants")
@Getter @Setter
public class Merchant {
    @Id private String code;
    @Column private String name;

    protected Merchant() {}

    public Merchant(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public boolean equals(Object o) {
        return this == o ||
                o != null && code != null &&
                        getClass() == o.getClass() &&
                        code.equals(getClass().cast(o).getCode());
    }

    public int hashCode() {
        return code.hashCode();
    }
}

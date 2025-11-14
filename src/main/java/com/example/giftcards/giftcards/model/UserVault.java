package com.example.giftcards.giftcards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table (name= "users")
@Getter @Setter
public class UserVault {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(unique = true, nullable = false) private String username;
    @Column(nullable = false) private String password;

    protected UserVault(){}
    public UserVault(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean equals( Object o ) {
        return this == o ||
                o != null && id != 0 &&
                        getClass() == o.getClass() && id.equals(getClass().cast( o ).getId()) &&
                        same( o );
    }

    public int hashCode() {
        return Long.hashCode( id );
    }

    protected boolean same( Object o ) {
        return username.equals( getClass().cast( o ).getUsername() );
    }
}


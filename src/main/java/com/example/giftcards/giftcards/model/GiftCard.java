package com.example.giftcards.giftcards.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table
@Getter @Setter
public class GiftCard {
    public static final String CargoImposible = "CargoImposible";
    public static final String InvalidCard = "InvalidCard";

    @Id private String id;
    @Column private int balance;
    @Column private String owner;

    //Como es una lista de Strings, no puede ser simplemente una columna. Es como otra tabla.
    @ElementCollection(fetch = FetchType.EAGER) //Cargalo de una, sin esperar
    @CollectionTable( name= "giftcard_charges", joinColumns =  @JoinColumn(name = "card_id"))
    @Column(name = "charges_description")
    private List<String> charges = new ArrayList<>();

    protected GiftCard() {}
    public GiftCard( String id, int initialBalance ) {
        this.id = id;
        balance = initialBalance;
    }

    public GiftCard charge( int anAmount, String description ) {
        if ( !owned() || ( balance - anAmount < 0 ) ) throw new RuntimeException( CargoImposible );

        balance = balance - anAmount;
        charges.add( description );

        return this;
    }

    public GiftCard redeem( String newOwner ) {
        if ( owned() ) throw new RuntimeException( InvalidCard );

        owner = newOwner;
        return this;
    }

    // proyectors
    public boolean owned() {                            return owner != null;                   }
    public boolean isOwnedBy( String aPossibleOwner ) { return owner.equals( aPossibleOwner );  }

    // accessors
    public String id() {            return id;      }
    public int balance() {          return balance; }
    public List<String> charges() { return charges; }

    public boolean equals( Object o ) {
        return this == o ||
                o != null && id != null &&
                        getClass() == o.getClass() && id.equals( getClass().cast( o ).getId());
                        // && same(o);
    }

    public int hashCode() {
        return id.hashCode();
    }

    //Depende si una persona solo puede tener una tarjeta, entonces ahi iria unique
    // y podemos ver si son la misma tarjeta con el owner
//    protected boolean same( Object o ) {
//        return owner.equals( getClass().cast( o ).getId() );
//    }


}

package com.example.giftcards.tuslibros.model;

import java.time.LocalDateTime;
import java.util.Map;


public class CartSession {
    private Clock clock;
    private Cart cart;
    private LocalDateTime lastAccess;

    public CartSession( Map<String, Integer> catalog, Clock clock ) {
        this.cart = new Cart( catalog );
        this.clock = clock;
        lastAccess = clock.now();
    }

    public void addToCart( int anAmount, String item ) {
        lastAccess = clock.now();
        cart.add( anAmount, item );
    }

    public Map content() {
        return cart.items();
    }
    public Cart getCart() { return cart; }

//    public Ticket checkOutCartWithCreditCardNumbered( TusLibrosSystemFacade systemFacade, CreditCard card ) {
//        final Cashier cashier = new Cashier();
//        return cashier.checkout( cart, card, systemFacade.salesBook() );
//    }

    public boolean isActive() {
        return lastAccess.plusMinutes( 30 ).isAfter( clock.now() );
    }
}

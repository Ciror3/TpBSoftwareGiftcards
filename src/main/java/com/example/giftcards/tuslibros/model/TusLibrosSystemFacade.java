package com.example.giftcards.tuslibros.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TusLibrosSystemFacade {
    public static String sessionHasExpiredErrorDescription = "Can not use the cart after minutes of inactivity";
    public static String invalidUserAndOrPasswordErrorDescription = "Invalid user and/or password";
    public static String invalidCartIdErrorDescription = "Invalid cart id";

    private final Map<String, String> validUsers;
    private final Map<String, Integer> catalog;
    private final List<Ticket>  salesBook;
    private int id = 0;
    private Map<Integer, CartSession> cartSessions = new HashMap();

    @Autowired private MerchantApi merchantApi;
    @Autowired private Clock clock;
    @Autowired private Cashier cashier;


    public TusLibrosSystemFacade() {
        this( Map.of( "Jhon", "Jpass", "Paul", "Ppass" ),
              Map.of( "bookA", 2, "bookB", 3 ),
              new ArrayList() );
    }
    public TusLibrosSystemFacade( Map<String, String> validUsers, Map<String, Integer> catalog, List<Ticket>  salesBook ) {
        this.validUsers = validUsers;
        this.catalog = catalog;
        this.salesBook = salesBook;
    }


    public int createCartFor( String user, String pass ) {
        checkValidUser( user, pass );
        int cartId = id++;
        cartSessions.put( cartId, new CartSession( catalog, clock ) );
        return cartId;

    }

    private void checkValidUser( String user, String pass ) {
        if ( !pass.equals( validUsers.get( user ) ) ) {
            throw new RuntimeException( invalidUserAndOrPasswordErrorDescription );
        }
    }

    public Map listCartIdentifiedAs( int cartId ) {
        return cartSessionIdentifiedAs( cartId ).content();
    }

    public void addItem( int cartId, String aBook, int anAmount ) {
        cartSessionIdentifiedAs( cartId )
                .addToCart( anAmount, aBook );
    }

    private CartSession cartSessionIdentifiedAs( int cartId ) {
        CartSession cartSession = cartSessions.computeIfAbsent( cartId, id1 -> { throw new RuntimeException( invalidCartIdErrorDescription );} );
        checkCartSessionIsActive( cartId, cartSession );
        return cartSession;
    }

    private void checkCartSessionIsActive( int cartId, CartSession cartSession ) {
        if ( !cartSession.isActive() ) {
            cartSessions.remove( cartId );
            throw new RuntimeException( sessionHasExpiredErrorDescription );
        }
    }

    public int checkOutCart( int cartId, String cardNumber, String owner, String month ) {

        Ticket ticket =  cashier.checkout( cartSessionIdentifiedAs( cartId ).getCart(),
                                           CreditCard.numberedOwnedExpiring( cardNumber, owner, month ),
                                           salesBook );

        removeCartId( cartId );

        return ticket.transactionId();
    }

    private void removeCartId( int cartId ) {
        cartSessions.remove( cartId );
    }

    public Clock clock() { return clock;    }
    public List<Ticket> salesBook() {        return salesBook;    }
    public MerchantApi merchantApi() { return merchantApi;    }

    public List<TicketLine> purchasesOf( String user, String pass ) {
        checkValidUser( user, pass );
        List<Ticket> sales = salesDoneBy( user );

        List soldItems = new ArrayList();
        sales.forEach( ticket -> soldItems.addAll( ticket.lines() ) );
        return soldItems;
    }

    private List<Ticket> salesDoneBy( String user ) {
        //"Esto es un indicio de que por ahi conviene empezar a pensar en modelar un SaleBook - Hernan"
        return salesBook.stream().filter( each -> each.owner() == user ).toList();
    }

    public void reset() {
        salesBook.clear();
        id = 0;
    }
}

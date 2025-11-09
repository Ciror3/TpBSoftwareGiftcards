package com.example.giftcards.tuslibros.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class TusLibrosSystemFacadeTest {

//public TusLibrosSystemFacade(
//public int createCartFor( String user, String pass ) {
//public Map listCartIdentifiedAs( int cartId ) {
//public void addItem( int cartId, String aBook, int anAmount ) {
//public int checkOutCart( int cartId, String cardNumber, String owner, String month ) {
//public List<TicketLine> purchasesOf( String user, String pass ) {
    @Autowired TusLibrosSystemFacade systemFacade;

    @MockBean MerchantApi merchantApi;
    @MockBean Clock clock;


    @BeforeEach public void  beforeEach() {
        systemFacade.reset();
        when( clock.now() ).then( it -> LocalDateTime.now() );
        when( clock.today() ).then( it -> LocalDate.now() );

    }

    @Test public void test01CanCreateCartWithValidUserAndPassword() {
        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );
        assertTrue( systemFacade.listCartIdentifiedAs( cartId ).isEmpty() );
    }

    @Test public void test02CanNotCreateCartWithInvalidUser() {
        assertThrowsLike( () -> systemFacade().createCartFor( "Ringo", "Rpass" ),
                          TusLibrosSystemFacade.invalidUserAndOrPasswordErrorDescription );
    }

    @Test public void test03CanNotCreateCartWithInvalidPassword() {
        assertThrowsLike( () -> systemFacade().createCartFor( "Jhon", "Rpass" ),
                          TusLibrosSystemFacade.invalidUserAndOrPasswordErrorDescription );

    }

    @Test public void test04CanAddItemsToACreatedCart() {
        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );

        systemFacade.addItem( cartId, "bookA", 2 );

        assertEquals( 2, systemFacade.listCartIdentifiedAs( cartId ).get( "bookA" ) );
    }

    @Test public void test05CanNotAddItemToNotCreatedCart() {
        assertThrowsLike( () -> systemFacade.addItem( 10, "bookA", 2 ),
                          TusLibrosSystemFacade.invalidCartIdErrorDescription );
    }

    @Test public void test06CanNotAddItemNotSellByTheStore() {
        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );

        assertThrowsLike( () -> systemFacade.addItem( cartId, "bookX", 2 ),
                          Cart.ItemNotInCatalog );
    }

    @Test public void test08CanNotListCartOfInvalidCartId() {
        assertThrowsLike( () -> systemFacade.listCartIdentifiedAs( 10 ),
                          TusLibrosSystemFacade.invalidCartIdErrorDescription );
    }

    @Test public void test09ListCartReturnsTheRightNumberOfItems() {
        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );

        systemFacade.addItem( cartId, "bookA", 2 );
        systemFacade.addItem( cartId, "bookB", 1 );

        Map content = systemFacade.listCartIdentifiedAs( cartId );

        assertEquals( 2, content.get( "bookA" ) );
        assertEquals( 1, content.get( "bookB" ) );
        assertEquals( 2, content.size() );
    }

    @Test public void test10CheckOutReturnsTransactionIdAndImpactsCustomerPurchases() {
        var cartId = systemFacade.createCartFor( "Jhon", "Jpass" );
        systemFacade.addItem( cartId, "bookA", 2 );

        when( merchantApi.debitFrom( any(), any() ) ).thenAnswer( it ->  42 );
        when( clock.now() ).then( it -> LocalDateTime.now() );

        systemFacade.checkOutCart( cartId, "1234567890123456", "Jhon", "202510" );

        List<TicketLine> purchases = systemFacade.purchasesOf( "Jhon", "Jpass" );
        assertEquals( 1, purchases.size() );
        assertEquals( "bookA", purchases.getFirst().getItem() );
        assertEquals( 2, purchases.getFirst().getQuantity() );
    }

    @Test public void test10_1_CanNotCheckoutAnAlreadyCheckedOutCart() {
        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );
        systemFacade.addItem( cartId, "bookA", 2 );

        when( merchantApi.debitFrom( any(), any() ) ).thenAnswer( it ->  42 );
        systemFacade.checkOutCart( cartId, "1234567890123456", "Jhon", "202510" );

        assertThrowsLike( () -> systemFacade.checkOutCart( cartId, "1234567890123456", "Jhon", "202510" ),
                          TusLibrosSystemFacade.invalidCartIdErrorDescription );

        List<TicketLine> purchases = systemFacade.purchasesOf( "Jhon", "Jpass" );
        assertEquals( 1, purchases.size() );
        assertEquals( "bookA", purchases.getFirst().getItem() );
        assertEquals( 2, purchases.getFirst().getQuantity() );
    }

    @Test public void test11CanNotCheckoutANotCreatedCart() {
        assertThrowsLike( () -> systemFacade.checkOutCart( 10, "1234567890123456", "Jhon", "202510" ),
                          TusLibrosSystemFacade.invalidCartIdErrorDescription );
    }

    @Test public void test12CanNotCheckoutAnEmptyCart() {
        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );

        assertThrowsLike( () -> systemFacade.checkOutCart( cartId, "1234567890123456", "Jhon", "202510" ),
                          Cashier.cartCanNotBeEmpty );
    }

    @Test public void test13CanNotCheckoutWithAnExpiredCreditCard() {
        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );

        systemFacade.addItem( cartId, "bookA", 2 );

        assertThrowsLike( () -> systemFacade.checkOutCart( cartId, "1234567890123456", "Jhon", "202410" ),
                          Cashier.canNotChargeAnExpiredCreditCard );
        assertTrue( systemFacade.purchasesOf( "Jhon", "Jpass" ).isEmpty() );
    }

    @Test public void test14ListPurchasesIncludesBoughtItems(){
        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );

        systemFacade.addItem( cartId, "bookA", 2 );
        systemFacade.addItem( cartId, "bookB", 1 );

        when( merchantApi.debitFrom( any(), any() ) ).thenAnswer( it ->  42 );
        systemFacade.checkOutCart( cartId, "1234567890123456", "Jhon", "202510" );

        List<TicketLine> purchases = systemFacade.purchasesOf( "Jhon", "Jpass" );
        assertEquals( "bookB", purchases.getFirst().getItem() );
        assertEquals( 1, purchases.getFirst().getQuantity() );
        assertEquals( "bookA", purchases.getLast().getItem() );
        assertEquals( 2, purchases.getLast().getQuantity() );
    }

    @Test public void test15CanNotListPurchasesOfInvalidCustomer() {
        assertThrowsLike( () -> systemFacade.purchasesOf( "Ringo", "Rpass" ),
                          TusLibrosSystemFacade.invalidUserAndOrPasswordErrorDescription );
    }

    @Test public void test16CanNotListPurchasesOfValidCustomerWithInvalidPassword() {
        assertThrowsLike( () -> systemFacade.purchasesOf( "Jhon", "Rpass" ),
                          TusLibrosSystemFacade.invalidUserAndOrPasswordErrorDescription );
    }

    @Test public void test17CanNotAddToCartWhenSessionIsExpired() {
//        TusLibrosSystemFacade systemFacade = systemFacade( twoTimesUseClock() );

        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );

        when( clock.now() ).then( it -> LocalDateTime.now().plusMinutes( 31 ) );

        assertThrowsLike( () -> systemFacade.addItem( cartId, "bookA", 2 ),
                          TusLibrosSystemFacade.sessionHasExpiredErrorDescription );
    }

    @Test public void test18CanNotListCartWhenSessionIsExpired() {
//        TusLibrosSystemFacade systemFacade = systemFacade( twoTimesUseClock() );
        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );

        when( clock.now() ).then( it -> LocalDateTime.now().plusMinutes( 31 ) );

        assertThrowsLike( () -> systemFacade.listCartIdentifiedAs( cartId ),
                          TusLibrosSystemFacade.sessionHasExpiredErrorDescription );
    }


    @Test public void test19CanNotCheckOutCartWhenSessionIsExpired() {

        int cartId = systemFacade.createCartFor( "Jhon", "Jpass" );
        systemFacade.addItem( cartId, "bookA", 2 );

        when( clock.now() ).then( it -> LocalDateTime.now().plusMinutes( 31 ) );

        assertThrowsLike( () -> systemFacade.checkOutCart( cartId, "1234567890123456", "Jhon", "202510" ),
                          TusLibrosSystemFacade.sessionHasExpiredErrorDescription );
        assertTrue( systemFacade.purchasesOf( "Jhon", "Jpass" ).isEmpty() );

    }

    private static Clock twoTimesUseClock() {
        return new Clock() {
            Iterator<LocalDateTime> it = List.of( LocalDateTime.now(), LocalDateTime.now().plusMinutes( 31 ) ).iterator();
            public LocalDateTime now() {
                return it.next();
            }
        };
    }

    private static TusLibrosSystemFacade systemFacade() {
        return systemFacade( new Clock() );
    }

    private static TusLibrosSystemFacade systemFacade( Clock clock ) {
        return new TusLibrosSystemFacade( Map.of( "Jhon", "Jpass", "Paul", "Ppass" ),
                                          Map.of( "bookA", 2, "bookB", 3 ),
                                          new ArrayList() );//,
//                                          new MerchantApi() {
//                                              public Integer debitFrom( CreditCard creditCard, Integer amount ) {
//                                                  return 42;
//                                              }
//                                          },
//                                          clock );
    }

    private void assertThrowsLike( Executable executable, String message ) {
        assertEquals( message,
                      assertThrows( Exception.class, executable )
                              .getMessage() );
    }
}

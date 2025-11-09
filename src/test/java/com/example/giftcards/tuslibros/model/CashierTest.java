package com.example.giftcards.tuslibros.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class CashierTest {
    @Autowired Cashier cashier;
    @MockBean
    MerchantApi merchantApi;

    @Test public void test01CanNotCheckoutAnEmptyCart() {
        assertThrowsLike( () -> cashier.checkout( CartTest.createCart(),
                                                  creditCardOn( "202510" ),
                                                  new ArrayList<>() ),
                          Cashier.cartCanNotBeEmpty );
    }
    // EFO: cashier responde por defecto un valor neutro
    @Test public void test02CalculatedTotalIsCorrect() {
        assertEquals( 2, cashier.checkout( CartTest.createCart().add( 2, "bookA" ),
                                           creditCardOn( "202510" ),
                                           new ArrayList<>() ).total() );
    }

    @Test public void test03CanNotCheckoutWithAnExpiredCreditCart() {
        assertThrowsLike( () -> cashier.checkout( CartTest.createCart().add( 2, "bookA" ),
                                                  creditCardOn( "202410" ),
                                                  new ArrayList<>() ),
                          Cashier.canNotChargeAnExpiredCreditCard );
    }

    @Test public void test04CheckoutRegistersASale() {
        List<Ticket> salesBook = new ArrayList<>();

        cashier.checkout( CartTest.createCart().add( 2, "bookA" ),
                          creditCardOn( "202510" ),
                          salesBook );

        assertEquals( 1, salesBook.size() );
        assertEquals( 2, salesBook.getFirst().total() );
    }

    @Test public void test05CashierChargesCreditCardUsingMerchantProcessor() {
        List merchantArgs = new ArrayList();
        CreditCard card = creditCardOn( "202510" );

        when( merchantApi.debitFrom( any(), any() ) )
                .thenAnswer( it -> {
                    merchantArgs.add( it.getArgument( 0 ) );
                    merchantArgs.add( it.getArgument( 1 ) );
                    return 42;
                } );

        int total = cashier.checkout( CartTest.createCart().add( 2, "bookA" ),
                                      card,
                                      new ArrayList<>() ).total();

        assertEquals( card, merchantArgs.getFirst() );
        assertEquals( total, merchantArgs.getLast() );
    }

    @Test public void test06CashierDoesNotSaleWhenTheCreditCardHasNoCredit() {
        List<Ticket> salesBook = new ArrayList<>();
        CreditCard card = creditCardOn( "202510" );

        when( merchantApi.debitFrom( any(), any() ) )
                .then( (any) -> { throw new RuntimeException( Cashier.creditCardHasNoCredit ); } );

        assertThrowsLike( () -> cashier.checkout( CartTest.createCart().add( 2, "bookA" ),
                                                  card,
                                                  salesBook ).total(),
                          Cashier.creditCardHasNoCredit );
        assertTrue( salesBook.isEmpty() );

    }

    private static CreditCard creditCardOn( String month ) {
        return CreditCard.numberedOwnedExpiring( "1234567890123456", "Pepe Grillo", month );
    }

    private void assertThrowsLike( Executable executable, String message ) {
        assertEquals( message,
                      assertThrows( Exception.class, executable )
                              .getMessage() );
    }
}

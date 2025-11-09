package com.example.giftcards.tuslibros.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class CreditCardTest {

    public String validCreditCardNumber = "1234567890123456";
    public String validOwnerName = "Pepe Grillo";

    @Test public void test01CreditCardNumberWithLessThan16DigitsIsNotValid() {
        assertThrowsLike( () -> CreditCard.numberedOwnedExpiring( "123456789012345", validOwnerName, "202510" ),
                          CreditCard.InvalidNumberErrorDescription );
    }

    @Test public void test02CreditCardNumberWithMoreThan16DigitsIsNotValid() {
        assertThrowsLike( () -> CreditCard.numberedOwnedExpiring( "12345678901234567", validOwnerName, "202510" ),
                          CreditCard.InvalidNumberErrorDescription );
    }

    @Test public void test03CreditCardNumberShouldBeDigitsOnly() {
        assertThrowsLike( () -> CreditCard.numberedOwnedExpiring( "a234567890123456", validOwnerName, "202510" ),
                          CreditCard.InvalidNumberErrorDescription );
    }

    @Test public void test04NameCanNotBeEmpty() {
        assertThrowsLike( () -> CreditCard.numberedOwnedExpiring( validCreditCardNumber, "", "202510" ),
                          CreditCard.InvalidOwnerNameErrorDescription );
    }

    @Test public void test05IsExpiredOnPastDate() {
        assertTrue( CreditCard.numberedOwnedExpiring( validCreditCardNumber, validOwnerName, "202410" )
                              .expiredOn( LocalDate.now() ) );
    }

    @Test public void test06IsNotExpiredOnExpirationMonth() {
        assertFalse( CreditCard.numberedOwnedExpiring( validCreditCardNumber, validOwnerName,
                                                       "" + LocalDate.now().getYear() + LocalDate.now().getMonthValue() )
                             .expiredOn( LocalDate.now() ) );
    }

    @Test public void test07IsNotExpiredOnFutureDate() {
        assertFalse( CreditCard.numberedOwnedExpiring( validCreditCardNumber, validOwnerName,
                                                       "" + ( LocalDate.now().getYear() + 1 ) + LocalDate.now().getMonthValue() )
                             .expiredOn( LocalDate.now() ) );
    }

    @Test public void test08CanGetCreditCardInfoFromIt() {
        CreditCard creditCard = CreditCard.numberedOwnedExpiring( validCreditCardNumber, validOwnerName, "202410" );
        assertEquals( validCreditCardNumber, creditCard.getNumber() );
        assertEquals( validOwnerName, creditCard.getOwner() );
        assertEquals( "202410", creditCard.getMonth() );
    }

    private void assertThrowsLike( Executable executable, String message ) {
        assertEquals( message,
                      assertThrows( Exception.class, executable )
                              .getMessage() );
    }

}

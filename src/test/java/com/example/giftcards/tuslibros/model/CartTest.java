package com.example.giftcards.tuslibros.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class CartTest {
    private static Map defaultCatalog = Map.of( "bookA", 1, "bookB", 2 );

    @Test public void test01NewCartsAreCreatedEmpty() {
        assertTrue( createCart().isEmpty() );
    }

    @Test public void test02CanNotAddItemsThatDoNotBelongToStore() {
        assertThrowsLike( () -> createCart().add( "book3" ), Cart.ItemNotInCatalog );
    }

    @Test public void test03AfterAddingAnItemTheCartIsNotEmptyAnymore() {
        assertFalse( createCart().add( "bookA" ).isEmpty() );
    }

    @Test public void test04CanNotAddNonPositiveNumberOfItems() {
        assertThrowsLike( () -> createCart().add( 0, "bookA" ), Cart.InvalidNumber );
    }

    @Test public void test06CartRemembersAddedItems() {
        assertTrue( createCart().add( "bookA" ).includes( "bookA" ) );
    }

    @Test public void test07CartDoesNotHoldNotAddedItems() {
        assertFalse( createCart().includes( "bookA" ) );
    }

    @Test public void test08CartRemembersTheNumberOfAddedItems() {
        assertEquals( 2, createCart().add( 2, "bookA" ).occurrencesOf( "bookA" ) );
    }

    private void assertThrowsLike( Executable executable, String message ) {
        assertEquals( message,
                      assertThrows( Exception.class, executable )
                              .getMessage() );
    }

    public static Cart createCart() {
        return Cart.acceptingItemsOf( defaultCatalog );
    }
}

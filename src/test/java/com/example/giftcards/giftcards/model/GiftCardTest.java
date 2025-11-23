package com.example.giftcards.giftcards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GiftCardTest {

    @Test public void aSimpleCard() {
        assertEquals( 10, newCard().balance() );
    }

    @Test public void aSimpleIsNotOwnedCard() {
        assertFalse( newCard().owned() );
    }

    @Test public void cannotChargeUnownedCards() {
        GiftCard aCard = newCard();
        assertThrows( RuntimeException.class, () -> aCard.charge( 2, "Un cargo" ) );
        assertEquals( 10, aCard.balance() );
        assertTrue( aCard.charges().isEmpty() );
    }

    @Test
    public void chargeACard() {
        GiftCard aCard = newCard();

        // Asegúrate de crear un UserVault válido
        UserVault bob = new UserVault("Bob", "password123");
        aCard.redeem( bob );

        // CAMBIO 1: El método charge ahora requiere el nombre del Merchant como 3er parámetro
        aCard.charge( 2, "Un cargo");

        assertEquals( 8, aCard.balance() );

        // CAMBIO 2: Igual que arriba, accede a .getDescription() del objeto Charge
        assertEquals( "Un cargo", aCard.charges().getLast().getDescription() );
    }

    @Test public void cannotOverrunACard() {
        GiftCard aCard = newCard();
        assertThrows( RuntimeException.class, () -> aCard.charge( 11, "Un cargo" ) );
        assertEquals( 10, aCard.balance() );
    }

    private GiftCard newCard() {
        return new GiftCard( "GC1", 10 );
    }

}

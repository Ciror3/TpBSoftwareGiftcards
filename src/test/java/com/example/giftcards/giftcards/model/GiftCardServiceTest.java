package com.example.giftcards.giftcards.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.BeforeEach;

import java.util.Random;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(GiftCardService.class)
public class GiftCardServiceTest {
    Random rnd = new Random(Instant.now().getEpochSecond());
    @Autowired
    protected GiftCardService service;

    @BeforeEach
    public void setUp() {
        service.findAll().forEach(service::delete);
    }

    protected GiftCard newGiftCard() {
        return new GiftCard("GC1" + rnd.nextInt(10000),100);
    }

    protected GiftCard savedGiftCard() {
        return service.save(newGiftCard());
    }

    @Test
    public void testEntitySave() {
        GiftCard model = newGiftCard();
        GiftCard retrieved = service.save( model );
        assertNotNull( retrieved.getId() );
        assertNotNull( model.getId() );
        assertEquals( retrieved, model );
    }

    @Test public void testDeletionByObject() {
        GiftCard model = savedGiftCard();

        service.delete( model );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );

    }

    @Test public void testDeletionById() {
        GiftCard model = savedGiftCard();

        service.deleteById( model.getId() );
        assertThrows( RuntimeException.class, () -> service.getById( model.getId() ) );

    }

    @Test public void testFindAll() {
        GiftCard model = savedGiftCard();
        List list = service.findAll();
        assertFalse( list.isEmpty() );
        assertTrue( list.contains( model ) );
    }

}

package com.example.giftcards.giftcards.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.BeforeEach;

import java.util.Random;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(GiftCardService.class)
public class GiftCardServiceTest extends ModelServiceTest<GiftCard, String, GiftCardService> {

    Random rnd = new Random(Instant.now().getEpochSecond());

    @Autowired
    protected GiftCardService service;

    @BeforeEach
    public void setUp() {
        service.findAll().forEach(service::delete);
    }

    @Override
    protected GiftCard newSample() {
        return new GiftCard("GC1" + rnd.nextInt(10000), 100);
    }
}

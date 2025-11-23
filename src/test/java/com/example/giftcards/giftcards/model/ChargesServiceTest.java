package com.example.giftcards.giftcards.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChargesServiceTest extends ModelServiceTest<Charges, Long, ChargesService> {
    @Autowired private GiftCardService giftCardService;
    @Autowired private UserService userService;

    @Override
    protected Charges newSample() {
        UserVault user = new UserVault("TestUser_" + Math.random(), "pass");
        userService.save(user);
        GiftCard card = new GiftCard("GC_" + Math.random(), 100);
        card.redeem(user);
        giftCardService.save(card);

        return new Charges(10,"Cargo de prueba",card);
    }
}

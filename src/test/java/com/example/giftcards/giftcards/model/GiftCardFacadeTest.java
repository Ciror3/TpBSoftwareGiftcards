package com.example.giftcards.giftcards.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class GiftCardFacadeTest {
    public static final String BOB_USER = "Bob";
    public static final String BOB_PASS = "BobPass";
    public static final String KEVIN_USER = "Kevin";
    public static final String KEVIN_PASS = "KevPass";
    public static final String MERCHANT_M1 = "M1";
    public static final String CARD_GC1 = "GC1";
    public static final String CARD_GC2 = "GC2";

    @Autowired private GiftCardFacade facade;
    @Autowired private GiftCardService giftCardService;
    @Autowired private UserService userService;
    @Autowired private MerchantService merchantService;

    @MockBean private Clock clock;

    private void cleanAndLoadData() {
        giftCardService.findAll().forEach(giftCardService::delete);
        userService.findAll().forEach(userService::delete);

        userService.save(new UserVault(BOB_USER, BOB_PASS));
        userService.save(new UserVault(KEVIN_USER, KEVIN_PASS));
        merchantService.save(new Merchant(MERCHANT_M1, "Starbucks"));
        giftCardService.save(new GiftCard(CARD_GC1, 10));
        giftCardService.save(new GiftCard(CARD_GC2, 5));
    }

    @BeforeEach
    public void setUp()
    {
        when(clock.now()).thenReturn(LocalDateTime.now());
        cleanAndLoadData();
    }

    @Test public void userCanOpenASession() {
        assertNotNull( facade.login(BOB_USER, BOB_PASS) );
    }

    @Test public void unknownUserCannotOpenASession() {
        assertThrows( RuntimeException.class, () -> facade.login( "Stuart", "StuPass" ) );
    }

    @Test public void userCannotUseAnInvalidtoken() {
        assertThrows( RuntimeException.class, () -> facade.redeem( UUID.randomUUID(), CARD_GC1) );
        assertThrows( RuntimeException.class, () -> facade.balance( UUID.randomUUID(), CARD_GC1) );
        assertThrows( RuntimeException.class, () -> facade.details( UUID.randomUUID(), CARD_GC1) );
    }

    @Test public void userCannotCheckOnAlienCard() {
        UUID token = facade.login(BOB_USER, BOB_PASS);
        assertThrows( RuntimeException.class, () -> facade.balance( token, CARD_GC1) );
    }

    @Test public void userCanRedeeemACard() {
        UUID token = facade.login(BOB_USER, BOB_PASS);
        facade.redeem( token, CARD_GC1);
        assertEquals( 10, facade.balance( token, CARD_GC1) );
    }

    @Test public void userCanRedeeemASecondCard() {
        UUID token = facade.login(BOB_USER, BOB_PASS);
        facade.redeem( token, CARD_GC1);
        facade.redeem( token, CARD_GC2);

        assertEquals( 10, facade.balance( token, CARD_GC1) );
        assertEquals( 5, facade.balance( token, CARD_GC2) );
    }

    @Test public void multipleUsersCanRedeeemACard() {
        UUID bobsToken = facade.login(BOB_USER, BOB_PASS);
        UUID kevinsToken = facade.login(KEVIN_USER, KEVIN_PASS);

        facade.redeem( bobsToken, CARD_GC1);
        facade.redeem( kevinsToken, CARD_GC2);

        assertEquals( 10, facade.balance( bobsToken, CARD_GC1) );
        assertEquals( 5, facade.balance( kevinsToken, CARD_GC2) );
    }

    @Test public void unknownMerchantCantCharge() {
        assertThrows( RuntimeException.class, () -> facade.charge( "Mx", CARD_GC1, 2, "UnCargo" ) );

    }

    @Test public void merchantCantChargeUnredeemedCard() {
        assertThrows( RuntimeException.class, () -> facade.charge(MERCHANT_M1, CARD_GC1, 2, "UnCargo" ) );
    }

    @Test public void merchantCanChargeARedeemedCard() {
        UUID token = facade.login(BOB_USER, BOB_PASS);
        facade.redeem( token, CARD_GC1);
        facade.charge(MERCHANT_M1, CARD_GC1, 2, "UnCargo" );

        assertEquals( 8, facade.balance( token, CARD_GC1) );
    }

    @Test public void merchantCannotOverchargeACard() {
        UUID token = facade.login(BOB_USER, BOB_PASS);
        facade.redeem( token, CARD_GC1);

        assertThrows( RuntimeException.class, () -> facade.charge(MERCHANT_M1, CARD_GC1, 11, "UnCargo" ) );
    }

    @Test public void userCanCheckHisEmptyCharges() {
        UUID token = facade.login(BOB_USER, BOB_PASS);
        facade.redeem( token, CARD_GC1);

        assertTrue( facade.details( token, CARD_GC1).isEmpty() );
    }

    @Test public void userCanCheckHisCharges() {
        UUID token = facade.login(BOB_USER, BOB_PASS);
        facade.redeem( token, CARD_GC1);
        facade.charge(MERCHANT_M1, CARD_GC1, 2, "UnCargo" );

        assertEquals( "UnCargo", facade.details( token, CARD_GC1).getLast() );
    }

    @Test public void userCannotCheckOthersCharges() {
        facade.redeem( facade.login(BOB_USER, BOB_PASS), CARD_GC1);
        UUID kevinToken = facade.login(KEVIN_USER, KEVIN_PASS);

        assertThrows( RuntimeException.class, () -> facade.details( kevinToken, CARD_GC1) );
    }

    @Test public void tokenExpires() {
        when(clock.now()).thenReturn(LocalDateTime.now());
        UUID token = facade.login(KEVIN_USER, KEVIN_PASS);

        when(clock.now()).thenReturn(LocalDateTime.now().plusMinutes(16));
        assertThrows( RuntimeException.class, () -> facade.redeem( token, CARD_GC1) );
    }

}

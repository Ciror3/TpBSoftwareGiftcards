package com.example.giftcards.giftcards.controller;

import com.example.giftcards.giftcards.model.*;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class GiftcardsControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private GiftCardService giftCardService;
    @Autowired private UserService userService;
    @Autowired private MerchantService merchantService;

    private static final String BASE_URL = "/api/giftcards";
    private static final String CARD_ID = "GC1";
    private static final String INVALID_TOKEN = "tokenInvalid";

    private final String user = "Johnny";
    private final String password = "jojo";

    @BeforeEach
    public void setUp() {
        cleanAndLoadData();
    }

    private void cleanAndLoadData() {
        giftCardService.findAll().forEach(giftCardService::delete);
        userService.findAll().forEach(userService::delete);
        merchantService.findAll().forEach(merchantService::delete);

        userService.save(new UserVault(user, password));
        merchantService.save(new Merchant("M1", "Starbucks"));
        giftCardService.save(new GiftCard(CARD_ID, 10));
    }

    @Test
    void test01LoginCorrectly() throws Exception {
        String token = loginAndGetToken(user, password);
        assertDoesNotThrow(() -> UUID.fromString(token));
    }

        @Test
    void test02LoginIncorrectPass() throws Exception {
        performLogin(user, "IncorrectPassword")
            .andExpect(status().is(500));
    }

    @Test
    void test03RedeemCorrectly() throws Exception {
        String token = loginAndGetToken(user, password);
        performRedeem(CARD_ID, token)
                .andExpect(status().isOk());
    }

    @Test
    void test04RedeemWithInvalidToken() throws Exception {
        performRedeem(CARD_ID, INVALID_TOKEN)
                .andExpect(status().is(500));
    }

    @Test
    void test05RedeemWithoutHeader() throws Exception {
        mockMvc.perform(
                    post(BASE_URL + "/" + CARD_ID +"/redeem"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test06RedeemFailCardUnknow() throws Exception {
        String token = loginAndGetToken(user, password);
        performRedeem("GC1234", token)
                .andExpect(status().is(500));
    }

    @Test
    void test07GetBalanceCorrectly() throws Exception {
        String token = loginAndRedeemCard();

        performGetBalance(CARD_ID, token)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(10));
    }

    @Test
    void test08GetBalanceFailWithNotRedeemedCart() throws Exception {
        String token = loginAndGetToken(user, password);

        performGetBalance(CARD_ID, token)
                .andExpect(status().is(500));
    }

    @Test
    void test09GetBalanceFailInvalidToken() throws Exception {
        performGetBalance(CARD_ID, INVALID_TOKEN)
                .andExpect(status().is(500));
    }

    @Test
    void test10GetDetailsCorrectly() throws Exception {
        String token = loginAndRedeemCard();
        performGetDetails(CARD_ID, token)
                .andExpect(status().isOk());
    }

    @Test
    void test11GetDetailsFailInvalidToken() throws Exception {
        performGetDetails(CARD_ID, INVALID_TOKEN)
                .andExpect(status().is(500));
    }

    @Test
    void test12GetDetailsFailCardUnknown() throws Exception {
        String token = loginAndGetToken(user, password);

        performGetDetails("GC1234", token)
                .andExpect(status().is(500));
    }

    @Test
    void test13GetDetailsFailCardRedeemedByOther() throws Exception {
        userService.save(new UserVault("Bob", "BobPass"));
        String bobToken = loginAndGetToken("Bob", "BobPass");
        performRedeem(CARD_ID, bobToken);

        String johnnyToken = loginAndGetToken(user, password);
        performGetDetails(CARD_ID, johnnyToken)
                .andExpect(status().is(500));
    }

    @Test
    void test14ChargeCorrectly() throws Exception {
        String token = loginAndRedeemCard();

        performCharge(CARD_ID, "M1", 5, "Café", token)
                .andExpect(status().is(200));
    }

    @Test
    void test15ChargeWithInvalidMerchant() throws Exception {
        String token = loginAndRedeemCard();

        performCharge(CARD_ID, "MXX", 5, "Café", token)
                .andExpect(status().is(500));
    }

    @Test
    void test16ChargeOverBalance() throws Exception {
        String token = loginAndRedeemCard();

        performCharge(CARD_ID, "M1", 100, "Café", token)
                .andExpect(status().is(500));
    }



    private String loginAndGetToken(String user, String pass) throws Exception {
        var res = performLogin(user, pass)
                .andExpect(status().isOk())
                .andReturn();

        return new ObjectMapper().readTree(res.getResponse().getContentAsString())
                .get("token").asText();
    }

    private String loginAndRedeemCard() throws Exception {
        String token = loginAndGetToken(user, password);
        performRedeem(CARD_ID, token);
        return token;
    }

    private ResultActions performLogin(String user, String pass) throws Exception {
        return mockMvc.perform(post(BASE_URL + "/login")
                .param("user", user)
                .param("pass", pass));
    }

    private ResultActions performRedeem(String cardId, String token) throws Exception {
        return mockMvc.perform(
                post(BASE_URL + "/" + cardId + "/redeem")
                        .header("Authorization", "Bearer " + token)
        );
    }

    private ResultActions performGetBalance(String cardId, String token) throws Exception {
        return mockMvc.perform(
                get(BASE_URL + "/" + cardId + "/balance")
                        .header("Authorization", "Bearer " + token)
        );
    }

    private ResultActions performGetDetails(String cardId, String token) throws Exception {
        return mockMvc.perform(
                get(BASE_URL + "/" + cardId + "/details")
                        .header("Authorization", "Bearer " + token)
        );
    }

    private ResultActions performCharge(String cardId, String merchant, int amount,
                                        String description, String token) throws Exception {
        return mockMvc.perform(
                post(BASE_URL + "/" + cardId + "/charge")
                        .param("merchant", merchant)
                        .param("amount", String.valueOf(amount))
                        .param("description", description)
                        .header("Authorization", "Bearer " + token)
        );
    }
}




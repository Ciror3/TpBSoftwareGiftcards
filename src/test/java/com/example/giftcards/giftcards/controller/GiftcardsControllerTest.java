package com.example.giftcards.giftcards.controller;

import com.example.giftcards.giftcards.model.GifCardFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; // ✅ IMPORT CORRECTO

@WebMvcTest(GiftcardsController.class)
class GiftcardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GifCardFacade giftcardsSystemFacade;

    private final String user = "Johnny";
    private final String password = "jojo";

    @Test
    void test01LogIn() throws Exception {
        var result = mockMvc.perform(
                        post("/login")
                                .param("user", user)
                                .param("pass", password)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        // opcional: comprobar que el token es un UUID válido
        String token = new ObjectMapper()
                .readTree(result.getResponse().getContentAsString())
                .get("token").asText();

        assertDoesNotThrow(() -> UUID.fromString(token));
    }
}


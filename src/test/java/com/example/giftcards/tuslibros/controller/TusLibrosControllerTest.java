package com.example.giftcards.tuslibros.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TusLibrosControllerTest {

    @Autowired MockMvc mockMvc;


    @Test public void test01CanCreateCartWithValidUserAndPassword() throws Exception {


        var cartId = Integer.parseInt( mockMvc.perform( post( "/createCartFor" )
                                                                .contentType( MediaType.APPLICATION_JSON )
                                                                .param( "user", "Jhon" )
                                                                .param( "pass", "Jpass" ) )
                                               .andDo( print() )
                                               .andExpect( status().is( 200 ) )
                                               .andReturn()
                                               .getResponse()
                                               .getContentAsString() );


    }
}

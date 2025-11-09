package com.example.giftcards.tuslibros.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled
public class MerchantExternalTest {
    @Autowired MockMvc mockMvc;

    @Test void checkStatusExternal() throws Exception {
        assertEquals( "qué mirá", RestClient.create()
                                            .get()
                                            .uri( "http://localhost:8085/status" )
                                            .retrieve()
                                            .body( String.class ) );
    }

    @Test void checkPaymentExternel() throws Exception {
        assertEquals( "todo bien Paul", RestClient.create()
                                                  .post()
                                                  .uri( "http://localhost:8085/payment" )
                                                  .contentType( APPLICATION_JSON )
                                                  .body( "{ 'owner': 'Paul', 'amount': 100 }".replace( "'", "\"" ) )
                                                  .retrieve()
                                                  .body( Map.class ).get( "message" ) );
    }

}


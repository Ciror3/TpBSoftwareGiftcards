package com.example.giftcards.tuslibros.model;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MerchantApi {

    public Integer debitFrom( CreditCard card, Integer amount ) {
        return (Integer) RestClient.create()
                .post()
                .uri( "http://localhost:8085/payment" )
                .contentType(APPLICATION_JSON)
                .body( "{ 'owner': 'Paul', 'amount': 100 }".replace( "'", "\"" )  )
                .retrieve()
                .body( Map.class).get( "transaction" ) ;
    }

}

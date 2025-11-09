package com.example.giftcards.giftcards.controller;




import com.example.giftcards.giftcards.model.Clock;
import com.example.giftcards.giftcards.model.GifCardFacade;
import com.example.giftcards.giftcards.model.GiftCard;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class GiftcardsController {
    //    POST /api/giftcards/login?user=aUser&pass=aPassword
//    Devuelve un token válido
    private GifCardFacade giftcardsSystemFacade = newFacade();

    private static GifCardFacade newFacade() {
        return newFacade(new Clock());
    }
    public static GifCardFacade newFacade(Clock clock){
        return new GifCardFacade(
                new ArrayList<>(List.of(new GiftCard("GC1",10))),
                new HashMap<>(Map.of("Johnny","jojo")),
                new ArrayList<>(List.of("M1")),
                clock
        );
    }


    @PostMapping("/login") public ResponseEntity<Map<String, Object>> login(@RequestParam String user, @RequestParam String pass ) {
        UUID token = giftcardsSystemFacade.login(user, pass);
        return ResponseEntity.ok(Map.of("token", token.toString()));
    }
//    POST /api/giftcards/{cardId}/redeem
//    Reclama una tarjeta (header Authorization: Bearer <token>)
//    @PostMapping("/{cardId}/redeem") public ResponseEntity<String> redeemCard( @RequestHeader("Authorization") String header, @PathVariable String cardId ) {
//
////    GET /api/giftcards/{cardId}/balance
////    Consulta saldo de la tarjeta
//    @GetMapping("/{cardId}/balance") public ResponseEntity<Map<String, Object>> balance( @RequestHeader("Authorization") String header, @PathVariable String cardId ) {
//
////    GET /api/giftcards/{cardId}/details
////    Lista los movimientos de la tarjeta
//    @GetMapping("/{cardId}/details") public ResponseEntity<Map<String, Object>> details( @RequestHeader("Authorization") String tokenHeader, @PathVariable String cardId ) {
//
////    POST /api/giftcards/{cardId}/charge?merchant=MerchantCode&amount=anAmount&description=aDescriptio
////     Un merchant hace un cargo sobre la tarjeta
//    @PostMapping("/{cardId}/charge") public ResponseEntity<String> charge( @RequestParam String merchant, @RequestParam int amount, @RequestParam String description, @PathVariable String cardId ) {
}

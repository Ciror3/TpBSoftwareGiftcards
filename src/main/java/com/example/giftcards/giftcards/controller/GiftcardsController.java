package com.example.giftcards.giftcards.controller;

import com.example.giftcards.giftcards.model.GiftCardFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/giftcards")
public class GiftcardsController {
    @Autowired private GiftCardFacade giftcardsSystemFacade;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleIlegalArguments(RuntimeException ex) {
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    //    POST /api/giftcards/login?user=aUser&pass=aPassword
    //    Devuelve un token válido

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestParam String user, @RequestParam String pass ) {
        UUID token = giftcardsSystemFacade.login(user, pass);
        return ResponseEntity.ok(Map.of("token", token.toString()));
    }

//    POST /api/giftcards/{cardId}/redeem
//    Reclama una tarjeta (header Authorization: Bearer <token>)
    @PostMapping("/{cardId}/redeem")
    public ResponseEntity<String> redeemCard(@RequestHeader("Authorization") String header, @PathVariable String cardId ) {
        giftcardsSystemFacade.redeem(extractToken(header), cardId);
        return ResponseEntity.ok("Card redeemed successfully");
    }


////    GET /api/giftcards/{cardId}/balance
////    Consulta saldo de la tarjeta
    @GetMapping("/{cardId}/balance")
    public ResponseEntity<Map<String, Object>> balance( @RequestHeader("Authorization") String header, @PathVariable String cardId ) {
        return ResponseEntity.ok(Map.of("cardId", cardId,
                                        "balance", giftcardsSystemFacade.balance(extractToken(header), cardId) ));
    }

////    GET /api/giftcards/{cardId}/details
////    Lista los movimientos de la tarjeta
    @GetMapping("/{cardId}/details")
    public ResponseEntity<Map<String, Object>> details( @RequestHeader("Authorization") String tokenHeader, @PathVariable String cardId ) {
        return ResponseEntity.ok(Map.of( "cardId", cardId,
                                        "details", giftcardsSystemFacade.details(extractToken(tokenHeader), cardId) ));
    }

////    POST /api/giftcards/{cardId}/charge?merchant=MerchantCode&amount=anAmount&description=aDescriptio
////     Un merchant hace un cargo sobre la tarjeta
    @PostMapping("/{cardId}/charge")
    public ResponseEntity<String> charge( @RequestParam String merchant, @RequestParam int amount, @RequestParam String description, @PathVariable String cardId ) {
        giftcardsSystemFacade.charge(merchant, cardId, amount, description);

        return ResponseEntity.ok("Charge successful");
    }

    private UUID extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("InvalidToken");
        }
        String tokenString = authHeader.substring(7);
        return UUID.fromString(tokenString);
    }

}

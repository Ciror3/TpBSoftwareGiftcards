package com.example.giftcards.tuslibros.controller;

import com.example.giftcards.tuslibros.model.TusLibrosSystemFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TusLibrosController {
    @Autowired
    TusLibrosSystemFacade systemFacade;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleIllegalArgument(RuntimeException ex) {
        return ResponseEntity.internalServerError().body( ex.getMessage());
    }

    @PostMapping(value = "createCartFor", params = { "user", "pass" })
    public ResponseEntity createCartFor( @RequestParam String user, @RequestParam String pass ) {
        return ResponseEntity.ok( systemFacade.createCartFor( user, pass ) );
    }

    @GetMapping("listCartIdentifiedAs/{cartId}")
    public ResponseEntity listCartIdentifiedAs( @PathVariable int cartId ) {
        return ResponseEntity.ok( systemFacade.listCartIdentifiedAs( cartId ) );

    }

    @PostMapping(value = "addItem/{cartId}", params = { "item", "quantity" })
    public ResponseEntity addItem( @PathVariable int cartId, @RequestParam String item, @RequestParam int quantity ) {
        systemFacade.addItem( cartId, item, quantity );
        return ResponseEntity.ok( "" );
    }
//
//    @PostMapping(value = "checkOutCart/{cartId}", params = { "cardNumber", "owner", "month" })
//    public ResponseEntity checkOutCart( @PathVariable int cartId,
//                                        @RequestParam String cardNumber,
//                                        @RequestParam String owner,
//                                        @RequestParam String month ) {
//
//    }

}

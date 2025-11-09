package com.example.giftcards.tuslibros.model;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Cashier {
    public static String canNotChargeAnExpiredCreditCard = "Can not charge an expired credit card";
    public static String cartCanNotBeEmpty = "Can not check out an empty cart";
    public static String creditCardHasNoCredit = "Credit card has no credit";

    @Autowired private Clock clock;
    @Autowired private MerchantApi merchant;

    public Ticket checkout( Cart cart, CreditCard card, List<Ticket> salesBook ) {
        if ( cart.isEmpty() ) throw new RuntimeException( cartCanNotBeEmpty );
        if ( card.expiredOn( clock.today() ) ) throw new RuntimeException( canNotChargeAnExpiredCreditCard );
        Ticket ticket = createTicket( cart, card );
        ticket.transactionId( merchant.debitFrom( card, ticket.total() ) );
        salesBook.add( ticket );
        return ticket;
    }

    private Ticket createTicket( Cart cart, CreditCard card ) {
        return new Ticket( cart.items().entrySet().stream()
                                   .map( entry -> new TicketLine( entry.getKey(),
                                                                  entry.getValue(),
                                                                  cart.catalog().get( entry.getKey() ) * entry.getValue() ) )
                                   .toList(),
                           card.getOwner() );

    }

}

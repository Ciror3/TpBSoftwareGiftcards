package com.example.giftcards.tuslibros.model;

import java.util.List;

public class Ticket {
    private String owner;
    private List<TicketLine> lines;
    private int transactionId;

    public Ticket( List<TicketLine> items, String owner ) {
        lines = items;
        this.owner = owner;
    }

    public int total() {
        return lines.stream().mapToInt( each -> each.getValue() ).sum();
    }

    public int transactionId() {
        return transactionId;
    }

    public void transactionId( int txId ) {
        transactionId = txId;
    }
    public String owner() {
        return owner;
    }

    public List<TicketLine> lines() {
        return lines;
    }
}

package com.example.giftcards.tuslibros.model;

public class TicketLine {
    private String item;
    private int quantity;
    private int value;

    public TicketLine( String item, int quantity, int value ) {
        this.item = item;
        this.quantity = quantity;
        this.value = value;
    }

    public String getItem() {   return item;        }
    public int getQuantity() {  return quantity;    }
    public int getValue() {     return value;       }
}

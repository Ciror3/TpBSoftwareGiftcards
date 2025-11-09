package com.example.giftcards.tuslibros.model;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    public static final String InvalidNumber = "Invalid number of items";
    public static final String ItemNotInCatalog = "Item is not in catalog";
    private Map<String, Integer> catalog;
    private Map<String, Integer> items = new HashMap();

    public Cart( Map<String, Integer> defaultCatalog ) {
        catalog = defaultCatalog;
    }

    public static Cart acceptingItemsOf( Map defaultCatalog ) {
        return new Cart( defaultCatalog );
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Cart add( String item ) {
        return add( 1, item );
    }
    public Cart add( int ammount, String item ) {
        assertIsValidItem( item );
        assertIsValidQuantity( ammount );

        items.put( item, ammount );
        return this;
    }

    public void assertIsValidItem( Object anItem ) {
        if (!catalog.containsKey( anItem ) ) throw new RuntimeException( ItemNotInCatalog );
    }

    public void assertIsValidQuantity( int aQuantity ) {
        if ( aQuantity <= 0 ) throw new RuntimeException( InvalidNumber );
    }

    public boolean includes( String item ) {
        return items.containsKey( item );
    }

    public int occurrencesOf( String item ) {
        return items.get( item );
    }

    public Map<String, Integer> items() {
        return items;
    }

    public Map<String, Integer> catalog() {
        return catalog;
    }

    public int valorTotal() {
        return items.entrySet().stream()
                .mapToInt( each -> catalog().get( each.getKey() ) * each.getValue() ).sum();
    }
}

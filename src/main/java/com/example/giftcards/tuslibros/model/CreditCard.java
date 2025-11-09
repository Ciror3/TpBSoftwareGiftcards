package com.example.giftcards.tuslibros.model;

import java.time.LocalDate;


final public class CreditCard {
    public static String InvalidNumberErrorDescription = "Invalid credit card number. It must be 16 digits";
    public static String InvalidOwnerNameErrorDescription = "Owner name can not be empty";

    private String number;
    private String owner;
    private String month;

    public static CreditCard numberedOwnedExpiring( String number, String validOwnerName, String month ) {
       return new CreditCard( number, validOwnerName, month);
    }

    public CreditCard( String number, String validOwnerName, String month ) {
        assertIsValidNumber( number );
        assertIsValidOwnerName( validOwnerName );
        this.number = number;
        this.owner = validOwnerName;
        this.month = month;
    }

    public void assertIsValidNumber( String number ) {
        if ( number.length() != 16 ) throw new RuntimeException( InvalidNumberErrorDescription );

        if ( number.chars().anyMatch( any -> any > 58 || any < 48 )) throw new RuntimeException( InvalidNumberErrorDescription );

    }


    public void assertIsValidOwnerName( String anOwnerName ) {
        if ( anOwnerName.isEmpty() ) throw new RuntimeException( InvalidOwnerNameErrorDescription );
    }

    public boolean expiredOn( LocalDate now ) {
        return month.compareTo( "%d%02d".formatted( now.getYear(), now.getMonthValue() ) ) < 0;
    }


    public String getNumber() {     return number;  }
    public String getOwner() {      return owner;   }
    public String getMonth() {      return month;   }

}

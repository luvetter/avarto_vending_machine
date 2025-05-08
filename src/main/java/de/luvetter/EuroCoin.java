package de.luvetter;

public enum EuroCoin {
    TWO_EURO(200),
    ONE_EURO(100),
    FIFTY_CENTS(50),
    TWENTY_CENTS(20),
    TEN_CENTS(10)
    // Ignorieren wir für diese Aufgabe erstmal
//    FIVE_CENTS(5),
//    TWO_CENTS(2),
//    ONE_CENT(1)
    ;

     EuroCoin(final int cents) {
        this.cents = cents;
    }

    private final int cents;

    public int getCents() {
        return cents;
    }
}

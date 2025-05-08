package de.luvetter;

public record ProductAndChange(Object product, EuroCoin... change) {
    public ProductAndChange {
        if (product == null) {
            throw new IllegalArgumentException("Produkt darf nicht null sein");
        }

        if (change == null) {
            throw new IllegalArgumentException("Wechselgeld darf nicht null sein");
        }
    }
}

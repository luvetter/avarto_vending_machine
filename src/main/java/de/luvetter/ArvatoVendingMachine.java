package de.luvetter;

public class ArvatoVendingMachine {

    private final int numberOfSlots;

    public ArvatoVendingMachine(final int numberOfSlots) {
        if (numberOfSlots < 1) {
            throw new IllegalArgumentException("Die Anzahl der Slots muss mindestens 1 sein");
        }
        this.numberOfSlots = numberOfSlots;
    }

    public ProductAndChange buy(final int slot, final EuroCoins... coins) {
        if (slot < 0 || slot >= numberOfSlots) {
            throw new IllegalArgumentException("Bitte wähle einen Slot zwischen 0 und " + (numberOfSlots - 1));
        }
        if (coins == null || coins.length == 0) {
            throw new IllegalArgumentException("Bitte werfen Sie Geld ein");
        }
        return null;
    }
}

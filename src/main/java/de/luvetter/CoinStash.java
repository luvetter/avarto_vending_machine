package de.luvetter;

public class CoinStash {
    private int amount;

    public CoinStash(final int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void add(final int amount) {
        this.amount += amount;
    }

    public void remove(final int amount) {
        if (amount > this.amount) {
            throw new IllegalArgumentException("Nicht genügend Münzen vorhanden");
        }
        this.amount -= amount;
    }

    public int removeAll() {
        final int removed = amount;
        amount = 0;
        return removed;
    }

    public boolean isEmpty() {
        return amount == 0;
    }
}

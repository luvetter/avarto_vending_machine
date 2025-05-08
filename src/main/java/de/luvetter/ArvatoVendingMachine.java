package de.luvetter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Stream;

public class ArvatoVendingMachine {

    private final int                         numberOfSlots;
    private final Map<Integer, Queue<Object>> products     = new HashMap<>();
    private final Map<Integer, Integer>       prices       = new HashMap<>();
    private final CashRegister                cashRegister = new CashRegister();

    public ArvatoVendingMachine(final int numberOfSlots) {
        if (numberOfSlots < 1) {
            throw new IllegalArgumentException("Die Anzahl der Slots muss mindestens 1 sein");
        }
        this.numberOfSlots = numberOfSlots;
    }

    public ProductAndChange buy(final int slot, final EuroCoin... coins) {
        final Queue<Object> inventory = getInventory(slot);
        if (inventory.isEmpty()) {
            throw new IllegalStateException("Slot " + slot + " ist leer");
        }
        final int price = getPrice(slot);
        final EuroCoin[] change = cashRegister.getChange(price, coins);
        return new ProductAndChange(inventory.poll(), change);
    }

    public void addCoins(final EuroCoin... coins) {
        cashRegister.addCoins(coins);
    }

    public int emptyCoinType(final EuroCoin coin) {
        return cashRegister.emptyCoinType(coin);
    }

    public void setPrice(final int slot, final int cents) {
        validateSlotRange(slot);
        if (cents < 0) {
            throw new IllegalArgumentException("Der Preis muss positiv sein");
        }
        this.prices.put(slot, cents);
    }

    public int getPrice(final int slot) {
        validateSlotRange(slot);
        return this.prices.getOrDefault(slot, 0);
    }

    public void addProducts(final int slot, final Object... products) {
        final Queue<Object> inventory = getInventory(slot);
        filterNullValues(products).forEach(inventory::add);
    }

    public List<Object> listProducts(final int slot) {
        return getInventory(slot).stream().toList();
    }

    public void removeProducts(final int slot, final Object... products) {
        final Queue<Object> inventory = getInventory(slot);
        final List<Object> toBeRemoved = filterNullValues(products).toList();
        assertProductsAreRemoveable(slot, toBeRemoved, inventory);
        inventory.removeAll(toBeRemoved);
    }

    private Queue<Object> getInventory(final int slot) {
        validateSlotRange(slot);
        return this.products.computeIfAbsent(slot, k -> new ArrayDeque<>());
    }

    private void validateSlotRange(final int slot) {
        if (slot < 0 || slot >= numberOfSlots) {
            throw new IllegalArgumentException("Bitte wähle einen Slot zwischen 0 und " + (numberOfSlots - 1));
        }
    }

    private Stream<Object> filterNullValues(final Object[] products) {
        return Stream.ofNullable(products)
                       .flatMap(Arrays::stream)
                       .filter(Objects::nonNull);
    }

    private void assertProductsAreRemoveable(final int slot, final List<Object> toBeRemoved, final Queue<Object> inventory) {
        for (final Object product : toBeRemoved) {
            if (!inventory.remove(product)) {
                throw new IllegalArgumentException("Produkt " + product + " nicht im Slot " + slot + " vorhanden");
            }
        }
    }
}

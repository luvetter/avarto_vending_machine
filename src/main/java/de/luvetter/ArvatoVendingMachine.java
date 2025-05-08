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
    private final Map<Integer, Queue<Object>> products = new HashMap<>();
    private final Map<Integer, Integer>       prices   = new HashMap<>();
    private final Map<EuroCoins, Integer>     register = new EnumMap<>(EuroCoins.class);

    public ArvatoVendingMachine(final int numberOfSlots) {
        if (numberOfSlots < 1) {
            throw new IllegalArgumentException("Die Anzahl der Slots muss mindestens 1 sein");
        }
        this.numberOfSlots = numberOfSlots;
        for (final EuroCoins coin : EuroCoins.values()) {
            register.put(coin, 0);
        }
    }

    public ProductAndChange buy(final int slot, final EuroCoins... coins) {
        final Queue<Object> inventory = getInventory(slot);
        if (inventory.isEmpty()) {
            throw new IllegalStateException("Slot " + slot + " ist leer");
        }
        final int price = getPrice(slot);
        final int totalInserted = calculateTotalInserted(coins);
        validateInsetCoversPrice(slot, price, totalInserted);
        for (final EuroCoins coin : coins) {
            register.compute(coin, (euroCoins, integer) -> integer + 1);
        }
        final EuroCoins[] change = getChange(totalInserted - price);
        return new ProductAndChange(inventory.poll(), change);
    }

    public void addCoins(final EuroCoins... coins) {
        for (final EuroCoins coin : coins) {
            register.compute(coin, (euroCoins, integer) -> integer + 1);
        }
    }

    public int emptyCoin(final EuroCoins coin) {
        if (coin == null) {
            throw new IllegalArgumentException("Bitte geben Sie eine Münze an");
        }
        final int amount = register.getOrDefault(coin, 0);
        register.put(coin, 0);
        return amount;
    }

    private EuroCoins[] getChange(final int targetChangeSum) {
        int currentChangeSum = 0;
        final List<EuroCoins> change = new ArrayList<>();
        for (final EuroCoins coin : EuroCoins.values()) {
            final int remaining = targetChangeSum - currentChangeSum;
            final int maxPossibleCoins = remaining / coin.getCents();
            if (maxPossibleCoins == 0) {
                continue;
            }
            final int changeCoins = Math.min(register.get(coin) , maxPossibleCoins);
            register.put(coin, register.get(coin) - changeCoins);
            for (int i = 0; i < changeCoins; i++) {
                change.add(coin);
                currentChangeSum += coin.getCents();
            }
        }
        if (currentChangeSum != targetChangeSum) {
            throw new IllegalStateException("Nicht genug Wechselgeld im Automaten");
        }

        return change.toArray(new EuroCoins[0]);
    }

    private void validateInsetCoversPrice(final int slot, final int price, final int totalInserted) {
        if (price > 0 && totalInserted == 0) {
            throw new IllegalArgumentException("Bitte werfen Sie Geld ein");
        }
        if (totalInserted < price) {
            throw new IllegalArgumentException("Slot " + slot + " kostet " + price + " Cent");
        }
    }

    private int calculateTotalInserted(final EuroCoins[] coins) {
        return Stream.ofNullable(coins)
                       .flatMap(Arrays::stream)
                       .filter(Objects::nonNull)
                       .mapToInt(EuroCoins::getCents)
                       .sum();
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

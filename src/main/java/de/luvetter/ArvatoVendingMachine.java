package de.luvetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class ArvatoVendingMachine {

    private final int                        numberOfSlots;
    private final Map<Integer, List<Object>> products = new HashMap<>();

    public ArvatoVendingMachine(final int numberOfSlots) {
        if (numberOfSlots < 1) {
            throw new IllegalArgumentException("Die Anzahl der Slots muss mindestens 1 sein");
        }
        this.numberOfSlots = numberOfSlots;
    }

    public ProductAndChange buy(final int slot, final EuroCoins... coins) {
        validateSlotRange(slot);
        if (coins == null || coins.length == 0) {
            throw new IllegalArgumentException("Bitte werfen Sie Geld ein");
        }
        return null;
    }

    public void addProducts(final int slot, final Object... products) {
        validateSlotRange(slot);
        final List<Object> inventory = this.products.computeIfAbsent(slot, k -> new ArrayList<>());
        filterNullValues(products).forEach(inventory::add);
    }

    public List<Object> listProducts(final int slot) {
        validateSlotRange(slot);
        return Collections.unmodifiableList(this.products.getOrDefault(slot, Collections.emptyList()));
    }

    public void removeProducts(final int slot, final Object... products) {
        validateSlotRange(slot);
        this.products.get(slot).removeAll(filterNullValues(products).toList());
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
}

package de.luvetter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArvatoVendingMachine {

    private final List<ProductStash>    inventories;
    private final Map<Integer, Integer> prices       = new HashMap<>();
    private final CashRegister          cashRegister = new CashRegister();

    public ArvatoVendingMachine(final List<ProductStash> inventories) {
        if (inventories == null || inventories.isEmpty()) {
            throw new IllegalArgumentException("Die Anzahl der Slots muss mindestens 1 sein");
        }
        this.inventories = inventories;
    }

    public ProductAndChange buy(final int slot, final EuroCoin... coins) {
        final ProductStash inventory = getProductStash(slot);
        if (inventory.isEmpty()) {
            throw new IllegalStateException("Slot " + slot + " ist leer");
        }
        final int price = getPrice(slot);
        final EuroCoin[] change = cashRegister.getChange(price, coins);
        return new ProductAndChange(inventory.dropNext(), change);
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
        getProductStash(slot).addProducts(products);
    }

    public List<Object> listProducts(final int slot) {
        return getProductStash(slot).listProducts();
    }

    public void removeProducts(final int slot, final Object... products) {
        getProductStash(slot).removeProducts(products);
    }

    private ProductStash getProductStash(final int slot) {
        validateSlotRange(slot);
        return this.inventories.get(slot);
    }

    private void validateSlotRange(final int slot) {
        if (slot < 0 || slot >= inventories.size()) {
            throw new IllegalArgumentException("Bitte wähle einen Slot zwischen 0 und " + (inventories.size() - 1));
        }
    }
}

package de.luvetter;

import java.util.List;

// TODO: Java-Doc (überall)
public class ArvatoVendingMachine {

    private final List<Slot>   slots;
    private final CashRegister cashRegister = new CashRegister();

    public ArvatoVendingMachine(final List<ProductStash> inventories) {
        if (inventories == null || inventories.isEmpty()) {
            throw new IllegalArgumentException("Die Anzahl der Slots muss mindestens 1 sein");
        }
        this.slots = inventories.stream().map(Slot::new).toList();
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
        getSlot(slot).setPrice(cents);
    }

    public int getPrice(final int slot) {
        return getSlot(slot).getPrice();
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
        return getSlot(slot).inventory;
    }

    private Slot getSlot(final int slot) {
        validateSlotRange(slot);
        return this.slots.get(slot);
    }

    private void validateSlotRange(final int slot) {
        if (slot < 0 || slot >= slots.size()) {
            throw new IllegalArgumentException("Bitte wähle einen Slot zwischen 0 und " + (slots.size() - 1));
        }
    }

    private static class Slot {
        private       int          price;
        private final ProductStash inventory;

        public Slot(final ProductStash inventory) {
            this.inventory = inventory;
            this.price = 0;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(final int price) {
            if (price < 0) {
                throw new IllegalArgumentException("Der Preis muss positiv sein");
            }
            this.price = price;
        }
    }
}

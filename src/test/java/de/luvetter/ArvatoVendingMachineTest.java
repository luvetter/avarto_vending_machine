package de.luvetter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class ArvatoVendingMachineTest {
    @Test
    void constructor_should_throw_IllegalArgumentException_if_number_of_slot_is_less_than_one() {
        final int numberOfSlots = 0;

        assertThatThrownBy(() -> new ArvatoVendingMachine(numberOfSlots))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Die Anzahl der Slots muss mindestens 1 sein");
    }

    @Test
    void buy_should_throw_IllegalArgumentException_if_slot_is_negative() {
        final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(9);

        assertThatThrownBy(() -> vendingMachine.buy(- 1, EuroCoins.TWO_EURO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
    }

    @Test
    void buy_should_throw_IllegalArgumentException_if_slot_is_greater_than_available_slots() {
        final int numberOfSlots = 9;
        final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(numberOfSlots);

        assertThatThrownBy(() -> vendingMachine.buy(numberOfSlots, EuroCoins.TWO_EURO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
    }

    @NullAndEmptySource
    @ParameterizedTest
    void buy_should_throw_IllegalArgumentException_if_coins_is_missing(final EuroCoins... coins) {
        final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(9);

        assertThatThrownBy(() -> vendingMachine.buy( 0, coins))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bitte werfen Sie Geld ein");
    }

    @Test
    void addProducts_should_add_products_to_the_slot() {
        final int numberOfSlots = 9;
        final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(numberOfSlots);

        vendingMachine.addProducts(0, "Coke", "Pepsi");

        assertThat(vendingMachine.listProducts(0)).containsExactly("Coke", "Pepsi");
    }

    @Test
    void listProducts_should_return_empty_list_if_no_products_in_slot() {
        final int numberOfSlots = 9;
        final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(numberOfSlots);

        assertThat(vendingMachine.listProducts(0)).isEmpty();
    }

    @Test
    void removeProducts_should_remove_products_from_the_slot() {
        final int numberOfSlots = 9;
        final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(numberOfSlots);
        vendingMachine.addProducts(0, "Coke", "Pepsi");

        vendingMachine.removeProducts(0, "Coke");

        assertThat(vendingMachine.listProducts(0)).containsExactly("Pepsi");
    }
}
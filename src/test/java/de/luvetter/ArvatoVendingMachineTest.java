package de.luvetter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ArvatoVendingMachineTest {

    private static final int NUMBER_OF_SLOTS = 9;

    @Test
    void constructor_should_throw_IllegalArgumentException_if_number_of_slot_is_less_than_one() {
        final int numberOfSlots = 0;

        assertThatThrownBy(() -> new ArvatoVendingMachine(numberOfSlots))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Die Anzahl der Slots muss mindestens 1 sein");
    }

    @Nested
    class Buy {
        @ValueSource(ints = {-1, NUMBER_OF_SLOTS})
        @ParameterizedTest
        void should_throw_IllegalArgumentException_for_invalid_slot(final int slot) {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            assertThatThrownBy(() -> vendingMachine.buy(slot, EuroCoins.TWO_EURO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
        }

        @NullAndEmptySource
        @ParameterizedTest
        void should_throw_IllegalArgumentException_if_coins_is_missing(final EuroCoins... coins) {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            assertThatThrownBy(() -> vendingMachine.buy(0, coins))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte werfen Sie Geld ein");
        }
    }

    @Nested
    class AddProducts {

        @Test
        void should_add_products_to_the_slot() {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            vendingMachine.addProducts(0, "Coke", "Pepsi");

            assertThat(vendingMachine.listProducts(0)).containsExactly("Coke", "Pepsi");
        }

        @NullAndEmptySource
        @ParameterizedTest
        void should_ignore_null_and_empty_products(final Object... products) {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            vendingMachine.addProducts(0, products);

            assertThat(vendingMachine.listProducts(0)).isEmpty();
        }

        @Test
        void should_ignore_null_product() {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            vendingMachine.addProducts(0, "Coke", null, "Pepsi");

            assertThat(vendingMachine.listProducts(0)).containsExactly("Coke", "Pepsi");
        }

        @ValueSource(ints = {-1, NUMBER_OF_SLOTS})
        @ParameterizedTest
        void should_throw_IllegalArgumentException_for_invalid_slot(final int slot) {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            assertThatThrownBy(() -> vendingMachine.addProducts(slot))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
        }
    }

    @Nested
    class ListProducts {

        @Test
        void should_return_empty_list_if_no_products_in_slot() {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            assertThat(vendingMachine.listProducts(0)).isEmpty();
        }

        @ValueSource(ints = {-1, NUMBER_OF_SLOTS})
        @ParameterizedTest
        void should_throw_IllegalArgumentException_for_invalid_slot(final int slot) {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            assertThatThrownBy(() -> vendingMachine.listProducts(slot))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
        }
    }

    @Nested
    class RemoveProducts {


        @Test
        void should_remove_products_from_the_slot() {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);
            vendingMachine.addProducts(0, "Coke", "Pepsi");

            vendingMachine.removeProducts(0, "Coke");

            assertThat(vendingMachine.listProducts(0)).containsExactly("Pepsi");
        }

        @NullAndEmptySource
        @ParameterizedTest
        void should_ignore_null_and_empty_products(final Object... products) {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);
            vendingMachine.addProducts(0, "Coke", "Pepsi");

            vendingMachine.removeProducts(0, products);

            assertThat(vendingMachine.listProducts(0)).containsExactly("Coke", "Pepsi");
        }

        @Test
        void should_ignore_null_product() {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);
            vendingMachine.addProducts(0, "Coke", "Pepsi");

            vendingMachine.removeProducts(0, null, "Pepsi");

            assertThat(vendingMachine.listProducts(0)).containsExactly("Coke");
        }

        @ValueSource(ints = {-1, NUMBER_OF_SLOTS})
        @ParameterizedTest
        void should_throw_IllegalArgumentException_for_invalid_slot(final int slot) {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            assertThatThrownBy(() -> vendingMachine.removeProducts(slot))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
        }

        @Test
        void should_throw_IllegalArgumentException_if_slot_does_not_contain_product() {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);
            vendingMachine.addProducts(0, "Coke", "Pepsi");

            assertThatThrownBy(() -> vendingMachine.removeProducts(0, "Fanta"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Produkt Fanta nicht im Slot 0 vorhanden");
            assertThat(vendingMachine.listProducts(0)).containsExactly("Coke", "Pepsi");
        }
    }
}
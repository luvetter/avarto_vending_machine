package de.luvetter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ArvatoVendingMachineTest {

    private static final int NUMBER_OF_SLOTS = 9;

    ArvatoVendingMachine vendingMachine;

    @BeforeEach
    void createEmptyVendingMachine() {
        vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);
    }

    @Test
    void constructor_should_throw_IllegalArgumentException_if_number_of_slot_is_less_than_one() {
        final int numberOfSlots = 0;

        assertThatThrownBy(() -> new ArvatoVendingMachine(numberOfSlots))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Die Anzahl der Slots muss mindestens 1 sein");
    }

    @Nested
    class Buy {

        @Test
        void should_return_product_from_slot() {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.setPrice(0, 120);

            final ProductAndChange result = vendingMachine.buy(0, EuroCoin.ONE_EURO, EuroCoin.TWENTY_CENTS);

            assertThat(result).isNotNull()
                    .extracting(ProductAndChange::product)
                    .isEqualTo("Coke");
        }

        @Test
        void should_return_no_change_if_insert_matches_price() {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.setPrice(0, 120);

            final ProductAndChange result = vendingMachine.buy(0, EuroCoin.ONE_EURO, EuroCoin.TWENTY_CENTS);

            assertThat(result).isNotNull()
                    .extracting(ProductAndChange::change, InstanceOfAssertFactories.ARRAY)
                    .isEmpty();
        }

        @MethodSource("changeTestCases")
        @ParameterizedTest
        void should_return_change(final ChangeTestCase testCase) {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.setPrice(0, testCase.price);
            vendingMachine.addCoins(testCase.availableChange);

            final ProductAndChange result = vendingMachine.buy(0, testCase.insertedCoins);

            assertThat(result).isNotNull()
                    .extracting(ProductAndChange::change, InstanceOfAssertFactories.ARRAY)
                    .containsExactlyInAnyOrder(testCase.expectedChange);
        }

        static Stream<ChangeTestCase> changeTestCases() {
            return Stream.of(
                    new ChangeTestCase("return 80 cent in 20 cent coins")
                            .withPrice(120)
                            .withAvailableChange(EuroCoin.TWENTY_CENTS, EuroCoin.TWENTY_CENTS, EuroCoin.TWENTY_CENTS, EuroCoin.TWENTY_CENTS)
                            .withInsertedCoins(EuroCoin.TWO_EURO)
                            .withExpectedChange(EuroCoin.TWENTY_CENTS, EuroCoin.TWENTY_CENTS, EuroCoin.TWENTY_CENTS, EuroCoin.TWENTY_CENTS),
                    new ChangeTestCase("return 80 cent with just inserted 50 cent coin")
                            .withPrice(120)
                            .withAvailableChange(EuroCoin.TWENTY_CENTS, EuroCoin.TEN_CENTS)
                            .withInsertedCoins(EuroCoin.FIFTY_CENTS, EuroCoin.FIFTY_CENTS, EuroCoin.FIFTY_CENTS, EuroCoin.FIFTY_CENTS)
                            .withExpectedChange(EuroCoin.FIFTY_CENTS, EuroCoin.TWENTY_CENTS, EuroCoin.TEN_CENTS),
                    new ChangeTestCase("return 80 cent in 50 cent, 20 cent and 10 cent coin")
                            .withPrice(120)
                            .withAvailableChange(EuroCoin.FIFTY_CENTS, EuroCoin.TWENTY_CENTS, EuroCoin.TEN_CENTS)
                            .withInsertedCoins(EuroCoin.TWO_EURO)
                            .withExpectedChange(EuroCoin.FIFTY_CENTS, EuroCoin.TWENTY_CENTS, EuroCoin.TEN_CENTS),
                    new ChangeTestCase("return 120 cent with 2x 50 cent coins")
                            .withPrice(80)
                            .withAvailableChange(EuroCoin.FIFTY_CENTS, EuroCoin.FIFTY_CENTS, EuroCoin.TWENTY_CENTS, EuroCoin.TEN_CENTS)
                            .withInsertedCoins(EuroCoin.TWO_EURO)
                            .withExpectedChange(EuroCoin.FIFTY_CENTS, EuroCoin.FIFTY_CENTS, EuroCoin.TWENTY_CENTS),
                    new ChangeTestCase("return 120 cent with 1x 1 euro coin")
                            .withPrice(80)
                            .withAvailableChange(EuroCoin.ONE_EURO, EuroCoin.FIFTY_CENTS, EuroCoin.TWENTY_CENTS, EuroCoin.TEN_CENTS)
                            .withInsertedCoins(EuroCoin.TWO_EURO)
                            .withExpectedChange(EuroCoin.ONE_EURO, EuroCoin.TWENTY_CENTS)//,
                    // TODO: Wechselgeld algoryhtmus so erweitern, dass auch Münzgrößen geskippt werden, die von der Größe passen, aber nicht genug kleiner Gößen vergügbar sind, um die Summe passend zu bekommen
//                    new ChangeTestCase("return 80 cent with 4x 20 coins even if 50 is available, but no 10 cent")
//                            .withPrice(120)
//                            .withAvailableChange(EuroCoins.FIFTY_CENTS, EuroCoins.TWENTY_CENTS, EuroCoins.TWENTY_CENTS, EuroCoins.TWENTY_CENTS, EuroCoins.TWENTY_CENTS)
//                            .withInsertedCoins(EuroCoins.TWO_EURO)
//                            .withExpectedChange(EuroCoins.TWENTY_CENTS, EuroCoins.TWENTY_CENTS, EuroCoins.TWENTY_CENTS, EuroCoins.TWENTY_CENTS)
            );
        }

        @Test
        void should_remove_product_from_slot() {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.setPrice(0, 120);

            vendingMachine.buy(0, EuroCoin.ONE_EURO, EuroCoin.TWENTY_CENTS);

            assertThat(vendingMachine.listProducts(0)).isEmpty();
        }

        @Test
        void should_return_products_from_slot_in_order() {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.addProducts(0, "Coke", "Pepsi");
            vendingMachine.setPrice(0, 120);

            final ProductAndChange result1 = vendingMachine.buy(0, EuroCoin.ONE_EURO, EuroCoin.TWENTY_CENTS);
            final ProductAndChange result2 = vendingMachine.buy(0, EuroCoin.ONE_EURO, EuroCoin.TWENTY_CENTS);
            final ProductAndChange result3 = vendingMachine.buy(0, EuroCoin.ONE_EURO, EuroCoin.TWENTY_CENTS);

            assertThat(result1.product()).isEqualTo("Coke");
            assertThat(result2.product()).isEqualTo("Coke");
            assertThat(result3.product()).isEqualTo("Pepsi");
        }

        @Test
        void should_return_product_without_coins_if_price_is_free() {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.setPrice(0, 0);

            final ProductAndChange result = vendingMachine.buy(0);

            assertThat(result).isNotNull()
                    .extracting(ProductAndChange::product)
                    .isEqualTo("Coke");
        }

        @Test
        void should_throw_IllegalStateException_if_money_inventory_cannot_cover_change() {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.setPrice(0, 120);

            assertThatThrownBy(() -> vendingMachine.buy(0, EuroCoin.TWO_EURO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Nicht genug Wechselgeld im Automaten");
            assertThat(vendingMachine.listProducts(0)).containsExactly("Coke");
        }

        @Test
        void should_return_throw_IllegalArgumentException_if_coins_do_not_cover_price() {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.setPrice(0, 120);

            assertThatThrownBy(() -> vendingMachine.buy(0, EuroCoin.ONE_EURO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Slot 0 kostet 120 Cent");
        }

        @Test
        void should_throw_IllegalStateException_if_slot_is_empty() {
            assertThatThrownBy(() -> vendingMachine.buy(0, EuroCoin.TWO_EURO))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Slot 0 ist leer");
        }

        @ValueSource(ints = {-1, NUMBER_OF_SLOTS})
        @ParameterizedTest
        void should_throw_IllegalArgumentException_for_invalid_slot(final int slot) {
            assertThatThrownBy(() -> vendingMachine.buy(slot, EuroCoin.TWO_EURO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
        }

        @NullAndEmptySource
        @ParameterizedTest
        void should_throw_IllegalArgumentException_if_coins_is_missing(final EuroCoin... coins) {
            vendingMachine.setPrice(0, 100);
            vendingMachine.addProducts(0, "Coke");

            assertThatThrownBy(() -> vendingMachine.buy(0, coins))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte werfen Sie Geld ein");
        }
    }

    @Nested
    class SetPrice {
        @Test
        void should_set_price_for_the_slot() {
            vendingMachine.setPrice(0, 100);

            assertThat(vendingMachine.getPrice(0)).isEqualTo(100);
        }

        @Test
        void should_throw_IllegalArgumentException_if_price_is_negative() {
            assertThatThrownBy(() -> vendingMachine.setPrice(0, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Der Preis muss positiv sein");
        }

        @ValueSource(ints = {-1, NUMBER_OF_SLOTS})
        @ParameterizedTest
        void should_throw_IllegalArgumentException_for_invalid_slot(final int slot) {
            assertThatThrownBy(() -> vendingMachine.setPrice(slot, 100))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
        }
    }

    @Nested
    class GetPrice {
        @Test
        void should_return_zero_if_price_for_slot_is_not_configured() {
            assertThat(vendingMachine.getPrice(0)).isEqualTo(0);
        }

        @ValueSource(ints = {-1, NUMBER_OF_SLOTS})
        @ParameterizedTest
        void should_throw_IllegalArgumentException_for_invalid_slot(final int slot) {
            assertThatThrownBy(() -> vendingMachine.getPrice(slot))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
        }
    }

    @Nested
    class AddCoins {

        @EnumSource(EuroCoin.class)
        @ParameterizedTest
        void should_add_coins(final EuroCoin coin) {
            vendingMachine.addCoins(coin, coin);

            assertThat(vendingMachine.emptyCoin(coin)).isEqualTo(2);
        }
    }

    @Nested
    class EmptyCoin {
        @Test
        void should_return_coins_for_sold_products() {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.setPrice(0, 120);
            vendingMachine.buy(0, EuroCoin.ONE_EURO, EuroCoin.TWENTY_CENTS);

            assertThat(vendingMachine.emptyCoin(EuroCoin.TWO_EURO)).isEqualTo(0);
            assertThat(vendingMachine.emptyCoin(EuroCoin.ONE_EURO)).isEqualTo(1);
            assertThat(vendingMachine.emptyCoin(EuroCoin.FIFTY_CENTS)).isEqualTo(0);
            assertThat(vendingMachine.emptyCoin(EuroCoin.TWENTY_CENTS)).isEqualTo(1);
            assertThat(vendingMachine.emptyCoin(EuroCoin.TEN_CENTS)).isEqualTo(0);
        }

        @Test
        void should_return_not_return_coins_twice() {
            vendingMachine.addProducts(0, "Coke");
            vendingMachine.setPrice(0, 120);
            vendingMachine.buy(0, EuroCoin.ONE_EURO, EuroCoin.TWENTY_CENTS);

            assertThat(vendingMachine.emptyCoin(EuroCoin.ONE_EURO)).isEqualTo(1);
            assertThat(vendingMachine.emptyCoin(EuroCoin.ONE_EURO)).isEqualTo(0);
        }
    }

    @Nested
    class AddProducts {

        @Test
        void should_add_products_to_the_slot() {
            vendingMachine.addProducts(0, "Coke", "Pepsi");

            assertThat(vendingMachine.listProducts(0)).containsExactly("Coke", "Pepsi");
        }

        @NullAndEmptySource
        @ParameterizedTest
        void should_ignore_null_and_empty_products(final Object... products) {
            vendingMachine.addProducts(0, products);

            assertThat(vendingMachine.listProducts(0)).isEmpty();
        }

        @Test
        void should_ignore_null_product() {
            vendingMachine.addProducts(0, "Coke", null, "Pepsi");

            assertThat(vendingMachine.listProducts(0)).containsExactly("Coke", "Pepsi");
        }

        @ValueSource(ints = {-1, NUMBER_OF_SLOTS})
        @ParameterizedTest
        void should_throw_IllegalArgumentException_for_invalid_slot(final int slot) {
            assertThatThrownBy(() -> vendingMachine.addProducts(slot))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
        }
    }

    @Nested
    class ListProducts {

        @Test
        void should_return_empty_list_if_no_products_in_slot() {
            assertThat(vendingMachine.listProducts(0)).isEmpty();
        }

        @ValueSource(ints = {-1, NUMBER_OF_SLOTS})
        @ParameterizedTest
        void should_throw_IllegalArgumentException_for_invalid_slot(final int slot) {
            assertThatThrownBy(() -> vendingMachine.listProducts(slot))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bitte wähle einen Slot zwischen 0 und 8");
        }
    }

    @Nested
    class RemoveProducts {

        private static final String COKE  = "Coke";
        private static final String PEPSI = "Pepsi";

        @BeforeEach
        void addStockInFirstSlot() {
            vendingMachine.addProducts(0, COKE, PEPSI);
        }

        @Test
        void should_remove_products_from_the_slot() {
            vendingMachine.removeProducts(0, COKE);

            assertThat(vendingMachine.listProducts(0)).containsExactly(PEPSI);
        }

        @NullAndEmptySource
        @ParameterizedTest
        void should_ignore_null_and_empty_products(final Object... products) {
            vendingMachine.removeProducts(0, products);

            assertThat(vendingMachine.listProducts(0)).containsExactly(COKE, PEPSI);
        }

        @Test
        void should_ignore_null_product() {
            vendingMachine.removeProducts(0, null, PEPSI);

            assertThat(vendingMachine.listProducts(0)).containsExactly(COKE);
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
            assertThatThrownBy(() -> vendingMachine.removeProducts(0, "Fanta"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Produkt Fanta nicht im Slot 0 vorhanden");
            assertThat(vendingMachine.listProducts(0)).containsExactly(COKE, PEPSI);
        }

        // Regression Test für NullPointerException, wenn Slot noch nie befüllt wurde
        @Test
        void should_throw_IllegalArgumentException_if_slot_is_still_virgin() {
            final ArvatoVendingMachine vendingMachine = new ArvatoVendingMachine(NUMBER_OF_SLOTS);

            assertThatThrownBy(() -> vendingMachine.removeProducts(0, "Fanta"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Produkt Fanta nicht im Slot 0 vorhanden");
            assertThat(vendingMachine.listProducts(0)).isEmpty();
        }
    }

    static class ChangeTestCase implements Named<ChangeTestCase> {
        private String      name;
        private int        price;
        private EuroCoin[] availableChange;
        private EuroCoin[] insertedCoins;
        private EuroCoin[] expectedChange;

        ChangeTestCase(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ChangeTestCase getPayload() {
            return this;
        }

        ChangeTestCase withPrice(final int price) {
            this.price = price;
            return this;
        }

        ChangeTestCase withAvailableChange(final EuroCoin... availableChange) {
            this.availableChange = availableChange;
            return this;
        }

        ChangeTestCase withInsertedCoins(final EuroCoin... insertedCoins) {
            this.insertedCoins = insertedCoins;
            return this;
        }

        ChangeTestCase withExpectedChange(final EuroCoin... expectedChange) {
            this.expectedChange = expectedChange;
            return this;
        }
    }
}
package de.luvetter;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Stream;

public class ProductStash {

    private final Queue<Object> products = new ArrayDeque<>();

    public void add(final Object product) {
        products.add(product);
    }

    public List<Object> listProducts() {
        return products.stream().toList();
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    public Object dropNext() {
        return products.poll();
    }

    public void addProducts(final Object[] productsToAdd) {
        filterNullValues(productsToAdd).forEach(products::add);
    }

    public void removeProducts(final Object[] productsToRemove) {
        final List<Object> toBeRemoved = filterNullValues(productsToRemove).toList();
        assertProductsAreRemoveable(toBeRemoved);
        products.removeAll(toBeRemoved);
    }

    private Stream<Object> filterNullValues(final Object[] products) {
        return Stream.ofNullable(products)
                       .flatMap(Arrays::stream)
                       .filter(Objects::nonNull);
    }

    private void assertProductsAreRemoveable(final List<Object> toBeRemoved) {
        for (final Object product : toBeRemoved) {
            if (!products.contains(product)) {
                throw new IllegalArgumentException("Produkt " + product + " nicht im Slot vorhanden");
            }
        }
    }
}

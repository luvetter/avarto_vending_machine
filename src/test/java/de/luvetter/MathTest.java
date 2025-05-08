package de.luvetter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MathTest {

    @Test
    void one_plus_one_is_two() {
        final int result = 1 + 1;

        assertThat(result).isEqualTo(2);
    }
}

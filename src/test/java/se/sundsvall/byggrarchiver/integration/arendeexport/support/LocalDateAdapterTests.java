package se.sundsvall.byggrarchiver.integration.arendeexport.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class LocalDateAdapterTests {

    private final LocalDateAdapter localDateAdapter = new LocalDateAdapter();

    @Test
    void testUnmarshalWithNullInput() {
        assertThat(localDateAdapter.unmarshal(null)).isNull();
    }

    @Test
    void testUnmarshalWithNonNullInput() {
        var result = localDateAdapter.unmarshal("2023-06-06");

        assertThat(result.getYear()).isEqualTo(2023);
        assertThat(result.getMonthValue()).isEqualTo(6);
        assertThat(result.getDayOfMonth()).isEqualTo(6);
    }

    @Test
    void testMarshalWithNullInput() {
        assertThat(localDateAdapter.marshal(null)).isNull();
    }


    @Test
    void testMarshalWithNonNullInput() {
        var result = localDateAdapter.marshal(LocalDate.of(2023, 12, 24));

        assertThat(result).isEqualTo("2023-12-24");
    }
}

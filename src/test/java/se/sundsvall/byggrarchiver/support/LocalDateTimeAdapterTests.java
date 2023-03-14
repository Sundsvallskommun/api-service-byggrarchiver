package se.sundsvall.byggrarchiver.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class LocalDateTimeAdapterTests {

    private final LocalDateTimeAdapter localDateTimeAdapter = new LocalDateTimeAdapter();

    @Test
    void testUnmarshalWithNullInput() {
        assertThat(localDateTimeAdapter.unmarshal(null)).isNull();
    }

    @Test
    void testUnmarshalWithNonNullInput() {
        var result = localDateTimeAdapter.unmarshal("2011-12-03T10:15:30");

        assertThat(result.getYear()).isEqualTo(2011);
        assertThat(result.getMonthValue()).isEqualTo(12);
        assertThat(result.getDayOfMonth()).isEqualTo(3);
        assertThat(result.getHour()).isEqualTo(10);
        assertThat(result.getMinute()).isEqualTo(15);
        assertThat(result.getSecond()).isEqualTo(30);
    }

    @Test
    void testMarshalWithNullInput() {
        assertThat(localDateTimeAdapter.marshal(null)).isNull();
    }


    @Test
    void testMarshalWithNonNullInput() {
        var result = localDateTimeAdapter.marshal(LocalDateTime.of(2011, 12, 3, 10, 15, 30));

        assertThat(result).isEqualTo("2011-12-03T10:15:30");
    }
}

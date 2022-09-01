package se.sundsvall.byggrarchiver.service.util;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
class DateAdapterTest {

    @Test
    void testParseDate() {
        assertEquals(LocalDate.of(2022, 03, 27), DateAdapter.parseDate("2022-03-27"));
    }

    @Test
    void testPrintLocalDate() {
        assertEquals("2022-03-27", DateAdapter.printLocalDate(LocalDate.of(2022, 03, 27)));
    }

    @Test
    void testParseDateTime() {
        assertEquals(LocalDateTime.of(2016, 11, 9, 11, 44, 35, 797000000), DateAdapter.parseDateTime("2016-11-09T11:44:35.797"));
    }

    @Test
    void testPrintLocalDateTime() {
        assertEquals("2016-11-09T11:44:35.797", DateAdapter.printLocalDateTime(LocalDateTime.of(2016, 11, 9, 11, 44, 35, 797000000)));
    }

}

package org.thesis.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    @DisplayName("isEventInThePast test")
    void isEventInThePast() {
        assertTrue(Event.isEventInThePast(LocalDate.now().atStartOfDay()));
        assertTrue(Event.isEventInThePast(LocalDateTime.now().minus(1, ChronoUnit.MINUTES)));
        assertFalse(Event.isEventInThePast(LocalDateTime.now()));
        assertFalse(Event.isEventInThePast(LocalDateTime.now().plus(1, ChronoUnit.MINUTES)));
        assertFalse(Event.isEventInThePast(LocalDate.now().plusDays(1).atStartOfDay()));
        assertFalse(Event.isEventInThePast(LocalDate.now().plusWeeks(1).atStartOfDay()));
    }

    @Test
    @DisplayName("durationOfEventInMinutes test")
    void durationOfEventInMinutes() {
        assertTrue(Event.durationOfEventInMinutes(LocalDateTime.of(2022,11,10,12,00), LocalDateTime.of(2022,11,10,12,05)) == 5);
        assertTrue(Event.durationOfEventInMinutes(LocalDateTime.of(2022,11,10,12,00), LocalDateTime.of(2022,11,11,12,00)) == Event.MINUTES_IN_A_DAY);
        assertFalse(Event.durationOfEventInMinutes(LocalDateTime.of(2022,11,10,12,00), LocalDateTime.of(2022,11,10,12,05)) == 10);
        assertFalse(Event.durationOfEventInMinutes(LocalDateTime.of(2022,11,10,12,00), LocalDateTime.of(2022,11,10,12,05)) == 0);
    }

    @Test
    @DisplayName("localDateTimeParser test")
    void localDateTimeParser() {
        assertTrue(Event.localDateTimeParser("2022.11.10. 12:30", "yyyy.MM.dd. HH:mm").equals(LocalDateTime.of(2022, 11, 10, 12, 30)));
    }

    @Test
    @DisplayName("isTheTimeOfARegularEventCorrect test")
    void isTheTimeOfARegularEventCorrect() {
        assertTrue(Event.isTheTimeOfARegularEventCorrect(12,00,13,00));
        assertTrue(Event.isTheTimeOfARegularEventCorrect(01,00,12,00));
        assertFalse(Event.isTheTimeOfARegularEventCorrect(12,00,10,00));
        assertFalse(Event.isTheTimeOfARegularEventCorrect(00,00,00,00));
        assertFalse(Event.isTheTimeOfARegularEventCorrect(00,05,00,04));
    }

}
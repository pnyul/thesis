package org.thesis.model;

import javafx.beans.property.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * This class stores the user's events, which can be general events or exams.
 */
public class Event {

    public enum EventType {

        EXAM(0),

        GENERAL(1);

        private final Integer value;

        EventType(Integer value) {
            this.value = value;
        }

        public Integer getValue() {
            return value;
        }

    }

    public static final String DATE_TIME_PATTERN = "yyyy.MM.dd HH:mm";
    public static final int MINUTES_IN_A_DAY = 24 * 60;
    public static final int HOUR_TO_MINUTES = 60;
    public static final int MAX_DURATION_OF_GENERAL_EVENT_IN_MINUTE = 8 * 60;
    public static final int MAX_DURATION_OF_EXAM_EVENT_IN_MINUTE = 5 * 60;
    public static final String BORDER_COLOR_RED = "-fx-border-color: #ff0000";
    public static final String BORDER_COLOR_GREEN = "-fx-border-color: #00ff00";

    private IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private StringProperty nameOfEvent = new SimpleStringProperty(this, "nameOfEvent");
    private StringProperty username = new SimpleStringProperty(this, "username");
    private ObjectProperty<LocalDateTime> beginOfEvent = new SimpleObjectProperty<>(this, "beginOfEvent");
    private ObjectProperty<LocalDateTime> endOfEvent = new SimpleObjectProperty<>(this, "endOfEvent");
    private IntegerProperty type = new SimpleIntegerProperty(this, "type");
    private BooleanProperty active = new SimpleBooleanProperty(this, "active");

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getNameOfEvent() {
        return nameOfEvent.get();
    }

    public StringProperty nameOfEventProperty() {
        return nameOfEvent;
    }

    public void setNameOfEvent(String nameOfEvent) {
        this.nameOfEvent.set(nameOfEvent);
    }

    public LocalDateTime getBeginOfEvent() {
        return beginOfEvent.get();
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public ObjectProperty<LocalDateTime> beginOfEventProperty() {
        return beginOfEvent;
    }

    public void setBeginOfEvent(LocalDateTime beginOfEvent) {
        this.beginOfEvent.set(beginOfEvent);
    }

    public LocalDateTime getEndOfEvent() {
        return endOfEvent.get();
    }

    public ObjectProperty<LocalDateTime> endOfEventProperty() {
        return endOfEvent;
    }

    public void setEndOfEvent(LocalDateTime endOfEvent) {
        this.endOfEvent.set(endOfEvent);
    }

    public int getType() {
        return type.get();
    }

    public IntegerProperty typeProperty() {
        return type;
    }

    public void setType(int type) {
        this.type.set(type);
    }

    public boolean isActive() {
        return active.get();
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public void setActive(boolean active) {
        this.active.set(active);
    }

    public static String dateAndTimeToString(DatePicker date, ComboBox<String> hour, ComboBox<String> minute) {
        return date.getValue().toString().replace("-", ".") + " " + hour.getValue() + ":" + minute.getValue();
    }

    public static LocalDateTime localDateTimeParser(String dateAndTimeAsString, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateAndTimeAsString, dateTimeFormatter);
    }

    public static boolean isEventInTheExamPeriod(Integer type, LocalDateTime beginOfEvent, LocalDateTime endOfEvent, LocalDate beginOfExamPeriod, LocalDate endOfExamPeriod) {
        if (type.equals(EventType.EXAM.getValue()) && (beginOfEvent.isEqual(beginOfExamPeriod.atStartOfDay()) || beginOfEvent.isAfter(beginOfExamPeriod.atStartOfDay())) && endOfEvent.isBefore(endOfExamPeriod.plusDays(1).atStartOfDay())) {
            return true;
        }
        return type.equals(EventType.GENERAL.getValue()) && (beginOfEvent.isEqual(beginOfExamPeriod.atStartOfDay().minusWeeks(1)) || beginOfEvent.isAfter(beginOfExamPeriod.atStartOfDay().minusWeeks(1))) && endOfEvent.isBefore(endOfExamPeriod.plusDays(1).atStartOfDay());
    }

    public static boolean isEventInThePast(LocalDateTime beginOfEvent) {
        return beginOfEvent.isBefore(LocalDateTime.now());
    }

    public static boolean areTheDateAndTimeOfAOneTimeEventCorrect(DatePicker beginDate, DatePicker endDate, ComboBox<String> beginHour, ComboBox<String> beginMinute, ComboBox<String> endHour, ComboBox<String> endMinute) {

        boolean result = false;

        if (beginDate.getValue().isEqual(endDate.getValue())) {

            if (Integer.parseInt(beginHour.getValue()) == Integer.parseInt(endHour.getValue())) {

                if (Integer.parseInt(beginMinute.getValue()) == Integer.parseInt(endMinute.getValue())) {

                    beginDate.setStyle(BORDER_COLOR_RED);
                    endDate.setStyle(BORDER_COLOR_RED);
                    beginHour.setStyle(BORDER_COLOR_RED);
                    beginMinute.setStyle(BORDER_COLOR_RED);
                    endHour.setStyle(BORDER_COLOR_RED);
                    endMinute.setStyle(BORDER_COLOR_RED);

                } else if (Integer.parseInt(beginMinute.getValue()) < Integer.parseInt(endMinute.getValue())) {

                    beginDate.setStyle(BORDER_COLOR_GREEN);
                    endDate.setStyle(BORDER_COLOR_GREEN);
                    beginHour.setStyle(BORDER_COLOR_GREEN);
                    beginMinute.setStyle(BORDER_COLOR_GREEN);
                    endHour.setStyle(BORDER_COLOR_GREEN);
                    endMinute.setStyle(BORDER_COLOR_GREEN);

                    result = true;

                } else {

                    beginDate.setStyle(null);
                    endDate.setStyle(null);
                    beginHour.setStyle(null);
                    beginMinute.setStyle(BORDER_COLOR_RED);
                    endHour.setStyle(null);
                    endMinute.setStyle(BORDER_COLOR_RED);

                }

            } else if (Integer.parseInt(beginHour.getValue()) > Integer.parseInt(endHour.getValue())) {

                beginDate.setStyle(null);
                endDate.setStyle(null);
                beginHour.setStyle(BORDER_COLOR_RED);
                beginMinute.setStyle(null);
                endHour.setStyle(BORDER_COLOR_RED);
                endMinute.setStyle(null);

            } else {

                beginDate.setStyle(BORDER_COLOR_GREEN);
                endDate.setStyle(BORDER_COLOR_GREEN);
                beginHour.setStyle(BORDER_COLOR_GREEN);
                beginMinute.setStyle(BORDER_COLOR_GREEN);
                endHour.setStyle(BORDER_COLOR_GREEN);
                endMinute.setStyle(BORDER_COLOR_GREEN);

                result = true;

            }
        } else if (beginDate.getValue().isBefore(endDate.getValue())) {

            beginDate.setStyle(BORDER_COLOR_GREEN);
            endDate.setStyle(BORDER_COLOR_GREEN);
            beginHour.setStyle(BORDER_COLOR_GREEN);
            beginMinute.setStyle(BORDER_COLOR_GREEN);
            endHour.setStyle(BORDER_COLOR_GREEN);
            endMinute.setStyle(BORDER_COLOR_GREEN);

            result = true;

        } else {

            beginDate.setStyle(BORDER_COLOR_RED);
            endDate.setStyle(BORDER_COLOR_RED);

            if (Integer.parseInt(beginHour.getValue()) == Integer.parseInt(endHour.getValue())) {
                if (Integer.parseInt(beginMinute.getValue()) == Integer.parseInt(endMinute.getValue())) {
                    beginHour.setStyle(BORDER_COLOR_RED);
                    beginMinute.setStyle(BORDER_COLOR_RED);
                    endHour.setStyle(BORDER_COLOR_RED);
                    endMinute.setStyle(BORDER_COLOR_RED);

                } else if (Integer.parseInt(beginMinute.getValue()) < Integer.parseInt(endMinute.getValue())) {
                    beginHour.setStyle(BORDER_COLOR_GREEN);
                    beginMinute.setStyle(BORDER_COLOR_GREEN);
                    endHour.setStyle(BORDER_COLOR_GREEN);
                    endMinute.setStyle(BORDER_COLOR_GREEN);

                } else {
                    beginHour.setStyle(BORDER_COLOR_GREEN);
                    beginMinute.setStyle(BORDER_COLOR_RED);
                    endHour.setStyle(BORDER_COLOR_GREEN);
                    endMinute.setStyle(BORDER_COLOR_RED);
                }

            } else if (Integer.parseInt(beginHour.getValue()) > Integer.parseInt(endHour.getValue())) {
                beginHour.setStyle(BORDER_COLOR_RED);
                beginMinute.setStyle(BORDER_COLOR_RED);
                endHour.setStyle(BORDER_COLOR_RED);
                endMinute.setStyle(BORDER_COLOR_RED);

            } else {
                beginHour.setStyle(BORDER_COLOR_GREEN);
                beginMinute.setStyle(BORDER_COLOR_GREEN);
                endHour.setStyle(BORDER_COLOR_GREEN);
                endMinute.setStyle(BORDER_COLOR_GREEN);
            }

        }

        return result;
    }


    public static boolean isTheTimeOfARegularEventCorrect(Integer beginHour, Integer beginMinute, Integer endHour, Integer endMinute) {

        if (Objects.equals(beginHour, endHour)) {

            if (Objects.equals(beginMinute, endMinute)) {
                return false;
            } else if (beginMinute < endMinute) {
                return true;
            } else {
                return false;
            }

        } else if (beginHour > endHour) {
            return false;
        } else {
            return true;
        }

    }

    public static boolean areEventsColliding(List<Event> events, LocalDateTime begin, LocalDateTime end) {
        for (Event event : events) {
            if (event.isActive() && (begin.isBefore(event.getBeginOfEvent()) && end.isAfter(event.getBeginOfEvent())) || begin.isEqual(event.getBeginOfEvent()) || (begin.isAfter(event.getBeginOfEvent()) && begin.isBefore(event.getEndOfEvent()))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isTheEventUnique(List<Event> events, String nameOfNewEvent, LocalDateTime beginOfNewEvent, LocalDateTime endOfNewEvent) {
        for (Event event : events) {
            if (event.getNameOfEvent().equals(nameOfNewEvent) && event.getBeginOfEvent().equals(beginOfNewEvent) && event.getEndOfEvent().equals(endOfNewEvent)) {
                return false;
            }
        }
        return true;
    }

    public static int durationOfEventInMinutes(LocalDateTime beginOfEvent, LocalDateTime endOfEvent) {
        return (int) Duration.between(beginOfEvent, endOfEvent).toMinutes();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nameOfEvent=" + nameOfEvent +
                ", username=" + username +
                ", beginOfEvent=" + beginOfEvent +
                ", endOfEvent=" + endOfEvent +
                ", type=" + type +
                ", active=" + active +
                '}';
    }

}

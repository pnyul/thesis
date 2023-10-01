package org.thesis.model;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.thesis.model.Event.HOUR_TO_MINUTES;
import static org.thesis.model.Event.MINUTES_IN_A_DAY;
/**
 * This class manages and sorts lists. It creates the order of exams and events needed to set the calendar.
 */
public class SortingAlgorithm {

    private List<Subject> subjects;
    private List<Event> exams;
    private List<Event> generalEvents;
    private User user;
    private LocalDate current;
    private List<Event> examsTaken;

    public SortingAlgorithm(User user, List<Subject> subjects, List<Event> events) {
        this.user = user;
        this.current = LocalDate.now().isBefore(user.getBeginOfExamPeriod().minusWeeks(1)) ||
                LocalDate.now().isEqual(user.getBeginOfExamPeriod().minusWeeks(1)) ?
                user.getBeginOfExamPeriod().minusWeeks(1) : LocalDate.now();
        this.subjects = greedyStrategyForSubjectsOrder(subjects);
        this.generalEvents = filterAndOrderGeneralEventsByDate(copyEventObject(events));
        this.exams = filterAndOrderExamsByDate(copyEventObject(events));
        this.examsTaken = new ArrayList<>();
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    private List<Event> copyEventObject(List<Event> originalEvents) {

        List<Event> copyEvents = new ArrayList<>();

        for (Event origEvent : originalEvents) {
            copyEvents.add(origEvent);
        }

        return copyEvents;

    }

    private List<Subject> greedyStrategyForSubjectsOrder(List<Subject> subjects) {

        subjects.removeIf(subject -> subject.isSuccess());

        Comparator<Subject> subjectsComparator = Comparator.comparing(Subject::getExamsLeft)
                .thenComparing(Subject::isFurtherRegistrationPossible)
                .thenComparing(Comparator.comparingInt(Subject::getCredit)
                        .reversed());

        subjects.sort(subjectsComparator);

        return subjects;

    }


    private List<Event> filterAndOrderGeneralEventsByDate(List<Event> events) {

        events.removeIf(event -> (event.getBeginOfEvent().isBefore(LocalDateTime.now())));
        events.removeIf(event -> (event.getType() == Event.EventType.EXAM.getValue()));
        events.removeIf(event -> (!event.isActive()));

        boolean suppExamExist = false;

        for (Subject subject : this.subjects) {
            if (subject.getSupplementaryExamAllowed()) {
                suppExamExist = true;
            }
        }

        if (!suppExamExist) {
            events.removeIf(event -> (event.getBeginOfEvent().isEqual(this.user.getBeginOfSupplementaryExamPeriod().atStartOfDay()) || event.getBeginOfEvent().isAfter(this.user.getBeginOfSupplementaryExamPeriod().atStartOfDay())));
        }

        Comparator<Event> eventsComparator = Comparator.comparing(Event::getBeginOfEvent);
        events.sort(eventsComparator);

        return events;
    }


    private List<Event> filterAndOrderExamsByDate(List<Event> events) {

        events.removeIf(event -> (event.getBeginOfEvent().isBefore(LocalDateTime.now())));
        events.removeIf(event -> (event.getType() == Event.EventType.GENERAL.getValue()));
        events.removeIf(event -> (!event.isActive()));

        for (Subject subject : this.subjects) {
            if (subject.isSuccess()) {
                events.removeIf(event -> event.getNameOfEvent().equals(subject.getSubject()));
            }
        }

        for (Subject subject : this.subjects) {
            if (!subject.getSupplementaryExamAllowed()) {
                events.removeIf(event -> (event.getNameOfEvent().equals(subject.getSubject()) && (event.getBeginOfEvent().isEqual(this.user.getBeginOfSupplementaryExamPeriod().atStartOfDay()) || event.getBeginOfEvent().isAfter(this.user.getBeginOfSupplementaryExamPeriod().atStartOfDay()))));
            }
        }

        Comparator<Event> examsComparator = Comparator.comparing(Event::getBeginOfEvent);
        events.sort(examsComparator);

        return events;

    }


    public List<Event> proposedExamDatesWithGeneralEvents() {

        List<Event> recommendedOrderOfEvents = new ArrayList<>();

        int learntMinutes = 0;
        int availableMinutesOnGivenDay = 0;
        int examIndex = 0;

        for (Subject subject : this.subjects) {
            subject.setTimeNeededForLearning(subject.getTimeNeededForLearning() * HOUR_TO_MINUTES);
        }

        for (int i = 0; i < this.subjects.size(); i++) {

            while (learntMinutes < this.subjects.get(i).getTimeNeededForLearning()) {

                if ((this.current.getDayOfWeek() == DayOfWeek.SATURDAY && this.user.isRestDayOnSaturday()) || (this.current.getDayOfWeek() == DayOfWeek.SUNDAY && this.user.isRestDayOnSunday())) {
                    this.current = this.current.plusDays(1);
                    continue;
                }

                availableMinutesOnGivenDay = availableMinutesOnGivenDayCalculator(this.current);

                learntMinutes += (availableMinutesOnGivenDay >= user.getLearningTimePerDay() * HOUR_TO_MINUTES ? user.getLearningTimePerDay() * HOUR_TO_MINUTES : availableMinutesOnGivenDay);
                this.current = this.current.plusDays(1);
            }

            if (learntMinutes > this.subjects.get(i).getTimeNeededForLearning() && i + 1 < this.subjects.size()) {
                this.subjects.get(i + 1).setTimeNeededForLearning(this.subjects.get(i + 1).getTimeNeededForLearning() - (learntMinutes - this.subjects.get(i).getTimeNeededForLearning()));
            }

            learntMinutes = 0;

            while (examIndex < this.exams.size()) {
                if (this.exams.get(examIndex).getNameOfEvent().equals(this.subjects.get(i).getSubject()) && (this.exams.get(examIndex).getBeginOfEvent().isEqual(this.current.atStartOfDay()) || this.exams.get(examIndex).getBeginOfEvent().isAfter(this.current.atStartOfDay()))) {
                    if (!hasCollisionOnGivenDay(this.exams.get(examIndex), this.exams.get(examIndex).getBeginOfEvent().toLocalDate()) && !hasCollisionOnGivenDay(this.exams.get(examIndex), this.current) && hasFreeTimeOnExamDay(this.exams.get(examIndex))) {
                        recommendedOrderOfEvents.add(this.exams.get(examIndex));
                        this.examsTaken.add(this.exams.get(examIndex));

                        LocalDateTime nextDay = this.exams.get(examIndex).getBeginOfEvent().toLocalDate().plusDays(1).atStartOfDay();

                        while (this.exams.get(examIndex).getBeginOfEvent().isBefore(nextDay)) {
                            examIndex++;
                        }

                        break;
                    }
                }
                examIndex++;
            }

        }

        for (Event event : this.generalEvents) {
            if (event.isActive()) {
                recommendedOrderOfEvents.add(event);
            }
        }

        Comparator<Event> comparator = Comparator.comparing(event -> event.getBeginOfEvent());

        recommendedOrderOfEvents.sort(comparator);

        return recommendedOrderOfEvents;
    }

    private boolean hasFreeTimeOnExamDay(Event exam) {
        return availableMinutesOnGivenDayCalculator(exam.getBeginOfEvent().toLocalDate()) >= minutesCounter(exam, exam.getBeginOfEvent().toLocalDate());
    }

    private boolean hasCollisionOnGivenDay(Event exam, LocalDate givenDate) {

        for (Event event : this.generalEvents) {
            if ((exam.getBeginOfEvent().isBefore(event.getBeginOfEvent()) && exam.getEndOfEvent().isAfter(event.getBeginOfEvent())) || exam.getBeginOfEvent().isEqual(event.getBeginOfEvent()) || (exam.getBeginOfEvent().isAfter(event.getBeginOfEvent()) && exam.getBeginOfEvent().isBefore(event.getEndOfEvent()))) {
                return true;
            }
            if (event.getBeginOfEvent().toLocalDate().isAfter(givenDate)) {
                break;
            }
        }

        return false;

    }

    private int availableMinutesOnGivenDayCalculator(LocalDate givenDate) {

        int availableMinutesPerGivenDay = MINUTES_IN_A_DAY - user.getDurationOfDailyActivities() * HOUR_TO_MINUTES;

        for (Event generalEvent : this.generalEvents) {
            if (generalEvent.getBeginOfEvent().toLocalDate().isEqual(givenDate)) {
                availableMinutesPerGivenDay -= minutesCounter(generalEvent, givenDate);
            }
        }

        for (Event exam : this.examsTaken) {
            if (exam.getBeginOfEvent().toLocalDate().isEqual(givenDate)) {
                availableMinutesPerGivenDay -= minutesCounter(exam, givenDate);
            }
        }
        return availableMinutesPerGivenDay;

    }

    private int minutesCounter(Event event, LocalDate givenDate) {

        int durationOfEvents = 0;

        if ((event.getBeginOfEvent().isAfter(givenDate.atStartOfDay()) || event.getBeginOfEvent().isEqual(givenDate.atStartOfDay())) && (event.getEndOfEvent().isBefore(givenDate.plusDays(1).atStartOfDay()) || event.getEndOfEvent().isEqual(givenDate.plusDays(1).atStartOfDay()))) {
            durationOfEvents += (int) Duration.between(event.getBeginOfEvent(), event.getEndOfEvent()).toMinutes();
        }

        if (event.getBeginOfEvent().isBefore(givenDate.atStartOfDay()) && event.getEndOfEvent().isAfter(givenDate.atStartOfDay())) {
            durationOfEvents += (int) Duration.between(givenDate.atStartOfDay(), event.getEndOfEvent()).toMinutes();
        }

        if (event.getBeginOfEvent().isBefore(givenDate.plusDays(1).atStartOfDay()) && event.getEndOfEvent().isAfter(givenDate.plusDays(1).atStartOfDay())) {
            durationOfEvents += (int) Duration.between(event.getBeginOfEvent(), givenDate.plusDays(1).atStartOfDay()).toMinutes();
        }

        return durationOfEvents;

    }

    /**
     *
     * Method for testing purposes.
     */
    public boolean isAllExamsEnrolled() {
        if (this.subjects.size() == this.examsTaken.size()) {
            for (int i = 0; i < this.subjects.size(); i++) {
                if (!this.subjects.get(i).getSubject().equals(this.examsTaken.get(i).getNameOfEvent())) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;
    }

}




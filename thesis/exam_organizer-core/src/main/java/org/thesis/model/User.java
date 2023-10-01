package org.thesis.model;

import javafx.beans.property.*;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;

/**
 * This class stores a user.
 */
public class User {

    private IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private StringProperty username = new SimpleStringProperty(this, "username");
    private StringProperty password = new SimpleStringProperty(this, "password");
    private IntegerProperty maxPossibleExam = new SimpleIntegerProperty(this, "maxPossExam");
    private IntegerProperty maxPossibleExamPerSemester = new SimpleIntegerProperty(this, "maxPossSem");
    private ObjectProperty<LocalDate> beginOfExamPeriod = new SimpleObjectProperty<>(this, "beginOfExamPeriod");
    private ObjectProperty<LocalDate> endOfExamPeriod = new SimpleObjectProperty<>(this, "endOfExamPeriod");
    private ObjectProperty<LocalDate> beginOfSupplementaryExamPeriod = new SimpleObjectProperty<>(this, "beginOfSupplementaryExamPeriod");
    private ObjectProperty<LocalDate> endOfSupplementaryExamPeriod = new SimpleObjectProperty<>(this, "endOfSupplementaryExamPeriod");
    private IntegerProperty durationOfDailyActivities = new SimpleIntegerProperty(this, "durationOfDailyActivities");
    private IntegerProperty learningTimePerDay = new SimpleIntegerProperty(this, "learningTimePerDay");
    private BooleanProperty restDayOnSaturday = new SimpleBooleanProperty(this, "restDayOnSaturday");
    private BooleanProperty restDayOnSunday = new SimpleBooleanProperty(this, "restDayOnSunday");

    public LocalDate getBeginOfExamPeriod() {
        return beginOfExamPeriod.get();
    }

    public ObjectProperty<LocalDate> beginOfExamPeriodProperty() {
        return beginOfExamPeriod;
    }

    public void setBeginOfExamPeriod(LocalDate beginOfExamPeriod) {
        this.beginOfExamPeriod.set(beginOfExamPeriod);
    }

    public LocalDate getEndOfExamPeriod() {
        return endOfExamPeriod.get();
    }

    public ObjectProperty<LocalDate> endOfExamPeriodProperty() {
        return endOfExamPeriod;
    }

    public void setEndOfExamPeriod(LocalDate endOfExamPeriod) {
        this.endOfExamPeriod.set(endOfExamPeriod);
    }

    public LocalDate getBeginOfSupplementaryExamPeriod() {
        return beginOfSupplementaryExamPeriod.get();
    }

    public ObjectProperty<LocalDate> beginOfSupplementaryExamPeriodProperty() {
        return beginOfSupplementaryExamPeriod;
    }

    public void setBeginOfSupplementaryExamPeriod(LocalDate beginOfSupplementaryExamPeriod) {
        this.beginOfSupplementaryExamPeriod.set(beginOfSupplementaryExamPeriod);
    }

    public LocalDate getEndOfSupplementaryExamPeriod() {
        return endOfSupplementaryExamPeriod.get();
    }

    public ObjectProperty<LocalDate> endOfSupplementaryExamPeriodProperty() {
        return endOfSupplementaryExamPeriod;
    }

    public void setEndOfSupplementaryExamPeriod(LocalDate endOfSupplementaryExamPeriod) {
        this.endOfSupplementaryExamPeriod.set(endOfSupplementaryExamPeriod);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getMaxPossibleExamPerSemester() {
        return maxPossibleExamPerSemester.get();
    }

    public IntegerProperty maxPossibleExamPerSemesterProperty() {
        return maxPossibleExamPerSemester;
    }

    public void setMaxPossibleExamPerSemester(int maxPossibleExamPerSemester) {
        this.maxPossibleExamPerSemester.set(maxPossibleExamPerSemester);
    }

    public int getMaxPossibleExam() {
        return maxPossibleExam.get();
    }

    public IntegerProperty maxPossibleExamProperty() {
        return maxPossibleExam;
    }

    public void setMaxPossibleExam(int maxPossibleExam) {
        this.maxPossibleExam.set(maxPossibleExam);
    }

    public Integer getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
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

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public int getDurationOfDailyActivities() {
        return durationOfDailyActivities.get();
    }

    public IntegerProperty durationOfDailyActivitiesProperty() {
        return durationOfDailyActivities;
    }

    public void setDurationOfDailyActivities(int durationOfDailyActivities) {
        this.durationOfDailyActivities.set(durationOfDailyActivities);
    }

    public int getLearningTimePerDay() {
        return learningTimePerDay.get();
    }

    public IntegerProperty learningTimePerDayProperty() {
        return learningTimePerDay;
    }

    public void setLearningTimePerDay(int learningTimePerDay) {
        this.learningTimePerDay.set(learningTimePerDay);
    }

    public boolean isRestDayOnSaturday() {
        return restDayOnSaturday.get();
    }

    public BooleanProperty restDayOnSaturdayProperty() {
        return restDayOnSaturday;
    }

    public void setRestDayOnSaturday(boolean restDayOnSaturday) {
        this.restDayOnSaturday.set(restDayOnSaturday);
    }

    public boolean isRestDayOnSunday() {
        return restDayOnSunday.get();
    }

    public BooleanProperty restDayOnSundayProperty() {
        return restDayOnSunday;
    }

    public void setRestDayOnSunday(boolean restDayOnSunday) {
        this.restDayOnSunday.set(restDayOnSunday);
    }

    public static boolean isThePasswordCorrect(String givenPassword, String hashedPassword) {
        return BCrypt.checkpw(givenPassword, hashedPassword);
    }

    public static String passwordHasher(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username=" + username +
                ", password=" + password +
                ", maxPossibleExam=" + maxPossibleExam +
                ", maxPossibleExamPerSemester=" + maxPossibleExamPerSemester +
                ", beginOfExamPeriod=" + beginOfExamPeriod +
                ", endOfExamPeriod=" + endOfExamPeriod +
                ", beginOfSupplementaryExamPeriod=" + beginOfSupplementaryExamPeriod +
                ", endOfSupplementaryExamPeriod=" + endOfSupplementaryExamPeriod +
                ", durationOfDailyActivities=" + durationOfDailyActivities +
                ", learningTimePerDay=" + learningTimePerDay +
                ", restDayOnSaturday=" + restDayOnSaturday +
                ", restDayOnSunday=" + restDayOnSunday +
                '}';
    }

}
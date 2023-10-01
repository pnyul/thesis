package org.thesis.model;

import javafx.beans.property.*;

/**
 * This class stores a single course for a given user.
 */
public class Subject {

    private IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private StringProperty subject = new SimpleStringProperty(this, "subject");
    private StringProperty user = new SimpleStringProperty(this, "user");
    private IntegerProperty credit = new SimpleIntegerProperty(this, "credit");
    private IntegerProperty examsLeft = new SimpleIntegerProperty(this, "examsLeft");
    private IntegerProperty timeNeededForLearning = new SimpleIntegerProperty(this, "timeNeededForLearning");
    private BooleanProperty furtherRegistrationPossible = new SimpleBooleanProperty(this, "furtherRegistrationPossible");
    private BooleanProperty success = new SimpleBooleanProperty(this, "success");
    private BooleanProperty preRequirement = new SimpleBooleanProperty(this, "preRequirement");
    private BooleanProperty supplementaryExamAllowed = new SimpleBooleanProperty(this, "suppExamAllowed");

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getSubject() {
        return subject.get();
    }

    public StringProperty subjectProperty() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

    public String getUser() {
        return user.get();
    }

    public StringProperty userProperty() {
        return user;
    }

    public void setUser(String user) {
        this.user.set(user);
    }

    public int getCredit() {
        return credit.get();
    }

    public IntegerProperty creditProperty() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit.set(credit);
    }

    public int getExamsLeft() {
        return examsLeft.get();
    }

    public IntegerProperty examsLeftProperty() {
        return examsLeft;
    }

    public void setExamsLeft(int examsLeft) {
        this.examsLeft.set(examsLeft);
    }

    public int getTimeNeededForLearning() {
        return timeNeededForLearning.get();
    }

    public IntegerProperty timeNeededForLearningProperty() {
        return timeNeededForLearning;
    }

    public void setTimeNeededForLearning(int timeNeededForLearning) {
        this.timeNeededForLearning.set(timeNeededForLearning);
    }

    public boolean isFurtherRegistrationPossible() {
        return furtherRegistrationPossible.get();
    }

    public BooleanProperty furtherRegistrationPossibleProperty() {
        return furtherRegistrationPossible;
    }

    public void setFurtherRegistrationPossible(boolean furtherRegistrationPossible) {
        this.furtherRegistrationPossible.set(furtherRegistrationPossible);
    }

    public boolean isSuccess() {
        return success.get();
    }

    public BooleanProperty successProperty() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success.set(success);
    }

    public boolean isPreRequirement() {
        return preRequirement.get();
    }

    public BooleanProperty preRequirementProperty() {
        return preRequirement;
    }

    public void setPreRequirement(boolean preRequirement) {
        this.preRequirement.set(preRequirement);
    }

    public boolean getSupplementaryExamAllowed() {
        return supplementaryExamAllowed.get();
    }

    public BooleanProperty supplementaryExamAllowedProperty() {
        return supplementaryExamAllowed;
    }

    public void setSupplementaryExamAllowed(boolean supplementaryExamAllowed) {
        this.supplementaryExamAllowed.set(supplementaryExamAllowed);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", subject=" + subject +
                ", user=" + user +
                ", credit=" + credit +
                ", examsLeft=" + examsLeft +
                ", timeNeededForLearning=" + timeNeededForLearning +
                ", furtherRegistrationPossible=" + furtherRegistrationPossible +
                ", success=" + success +
                ", preRequirement=" + preRequirement +
                ", supplementaryExamAllowed=" + supplementaryExamAllowed +
                '}';
    }

}



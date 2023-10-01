package org.thesis.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.*;
import org.thesis.model.Event;
import org.thesis.model.Subject;
import org.thesis.model.User;
import org.thesis.utility.ComboBoxInitializer;
import org.thesis.utility.ModalWindow;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static org.thesis.App.modalStage;
import static org.thesis.model.Event.BORDER_COLOR_GREEN;
import static org.thesis.model.Event.BORDER_COLOR_RED;
import static org.thesis.model.User.isThePasswordCorrect;
import static org.thesis.utility.ComboBoxInitializer.setComboBox;
import static org.thesis.utility.StageAndSceneInitializer.*;

/**
 * This class is used to change the user's settings.
 */
public class SettingsWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(SettingsWindowController.class);

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @FXML
    private Button deleteProfileButton;

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private DatePicker beginOfExamPeriodDatePicker;

    @FXML
    private DatePicker endOfExamPeriodDatePicker;

    @FXML
    private DatePicker beginOfSuppExamPeriodDatePicker;

    @FXML
    private DatePicker endOfSuppExamPeriodDatePicker;

    @FXML
    private ComboBox<Integer> durationOfDailyActivitiesComboBox;

    @FXML
    private Label beginOfExamHintLabel;

    @FXML
    private Label endOfExamHintLabel;

    @FXML
    private Label beginOfSuppExamHintLabel;

    @FXML
    private Label endOfSuppExamHintLabel;

    @FXML
    private TextField usernameTextField;

    @FXML
    private ComboBox<Integer> maxSemComboBox;

    @FXML
    private ComboBox<Integer> maxExamComboBox;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField newPasswordAgainField;

    private UserDAO<User> userDAO;
    private SubjectDAO<Subject> subjectDAO;
    private List<Subject> subjects;
    private EventDAO<Event> eventDAO;
    private List<Event> events;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.userDAO = new UserDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.subjectDAO = new SubjectDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.subjects = this.subjectDAO.findAllSubjects(App.user.getUsername());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.eventDAO = new EventDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.events = this.eventDAO.findAllEvents(App.user.getUsername());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        saveButton.setGraphic(new ImageView("/icons/save.png"));
        Tooltip.install(this.saveButton, new Tooltip("Save"));
        backButton.setGraphic(new ImageView("/icons/back.png"));
        Tooltip.install(this.backButton, new Tooltip("Back"));
        deleteProfileButton.setGraphic(new ImageView("/icons/delete_trash.png"));
        Tooltip.install(this.deleteProfileButton, new Tooltip("Delete profile"));

        this.currentPasswordField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        this.newPasswordField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        this.newPasswordAgainField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);

        modalStage = new Stage();

        this.usernameTextField.setText(App.user.getUsername());
        this.usernameTextField.setDisable(true);
        Tooltip.install(this.newPasswordField, new Tooltip("7-20 character at least 1: a-z, 1: A-Z, 1: 0-9, and 1 spec. char. needed."));
        Tooltip.install(this.newPasswordAgainField, new Tooltip("7-20 character at least 1: a-z, 1: A-Z, 1: 0-9, and 1 spec. char. needed."));

        setComboBox(maxExamComboBox, 3, 9, App.user.getMaxPossibleExam() - 3, ComboBoxInitializer.Type.INTEGER.getValue());
        setComboBox(maxSemComboBox, 1, 4, App.user.getMaxPossibleExamPerSemester() - 1, ComboBoxInitializer.Type.INTEGER.getValue());
        setComboBox(durationOfDailyActivitiesComboBox, 10, 15, App.user.getDurationOfDailyActivities() - 10, ComboBoxInitializer.Type.INTEGER.getValue());

        setToDefault();

        this.beginOfExamPeriodDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> endOfExamPeriodDatePicker.setValue(beginOfExamPeriodDatePicker.getValue().plusDays(1)));
        this.endOfExamPeriodDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> beginOfSuppExamPeriodDatePicker.setValue(endOfExamPeriodDatePicker.getValue().plusDays(1)));
        this.beginOfSuppExamPeriodDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> endOfSuppExamPeriodDatePicker.setValue(beginOfSuppExamPeriodDatePicker.getValue().plusDays(1)));
        this.maxSemComboBox.valueProperty().addListener((observableValue, localDate, t1) -> maxExamComboBox.setValue(maxSemComboBox.getValue() < 3 ? 3 : maxSemComboBox.getValue()));

        Platform.runLater(() -> this.currentPasswordField.requestFocus());

        this.currentPasswordField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                newPasswordField.requestFocus();
            }
        });

        this.newPasswordField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                newPasswordAgainField.requestFocus();
            }
        });

        this.newPasswordAgainField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                maxSemComboBox.requestFocus();
                maxSemComboBox.show();
            }
        });


        this.maxSemComboBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                maxExamComboBox.requestFocus();
                maxExamComboBox.show();
            }
        });

        this.maxExamComboBox.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                beginOfExamPeriodDatePicker.requestFocus();
            }
        });

    }

    private void setToDefault() {

        this.beginOfExamPeriodDatePicker.setValue(App.user.getBeginOfExamPeriod());
        this.beginOfExamPeriodDatePicker.setEditable(false);
        this.endOfExamPeriodDatePicker.setValue(App.user.getEndOfExamPeriod());
        this.endOfExamPeriodDatePicker.setEditable(false);
        this.beginOfSuppExamPeriodDatePicker.setValue(App.user.getBeginOfSupplementaryExamPeriod());
        this.beginOfSuppExamPeriodDatePicker.setEditable(false);
        this.endOfSuppExamPeriodDatePicker.setValue(App.user.getEndOfSupplementaryExamPeriod());
        this.endOfSuppExamPeriodDatePicker.setEditable(false);

    }


    public void saveButtonClicked(ActionEvent actionEvent) throws SQLException {

        if (checkPasswords() && checkDate()) {

            User user = new User();
            user.setId(App.user.getId());
            user.setUsername(App.user.getUsername());

            if (!this.newPasswordField.getText().isEmpty()) {
                user.setPassword(User.passwordHasher(this.newPasswordField.getText()));
                App.user.setPassword(User.passwordHasher(this.newPasswordField.getText()));
            } else {
                user.setPassword(App.user.getPassword());
            }

            user.setMaxPossibleExam(this.maxExamComboBox.getValue());
            user.setMaxPossibleExamPerSemester(this.maxSemComboBox.getValue());

            if (this.maxExamComboBox.getValue() != App.user.getMaxPossibleExam()) {
                Subject subject;
                for (Subject subj : this.subjects) {
                    if (subj.getExamsLeft() > this.maxExamComboBox.getValue()) {
                        subject = subj;
                        subject.setExamsLeft(this.maxExamComboBox.getValue());
                        this.subjectDAO.updateSubject(subject);
                    }
                }
            }

            user.setBeginOfExamPeriod(this.beginOfExamPeriodDatePicker.getValue());
            user.setEndOfExamPeriod(this.endOfExamPeriodDatePicker.getValue());
            user.setBeginOfSupplementaryExamPeriod(this.beginOfSuppExamPeriodDatePicker.getValue());
            user.setEndOfSupplementaryExamPeriod(this.endOfSuppExamPeriodDatePicker.getValue());
            user.setDurationOfDailyActivities(this.durationOfDailyActivitiesComboBox.getValue());
            user.setLearningTimePerDay(App.user.getLearningTimePerDay());
            user.setRestDayOnSaturday(App.user.isRestDayOnSaturday());
            user.setRestDayOnSunday(App.user.isRestDayOnSunday());

            if (this.beginOfExamPeriodDatePicker.getValue().isAfter(App.user.getBeginOfExamPeriod()) || this.endOfSuppExamPeriodDatePicker.getValue().isBefore(App.user.getEndOfSupplementaryExamPeriod())) {
                modalStage = new Stage();
                modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "The dates given overlap with the original period.");
                setAndShowModalWindow(modalWindow);
                modalStage = new Stage();
                modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "May result in loss of events.");
                setAndShowModalWindow(modalWindow);
                for (Event event : this.events) {
                    if (event.getBeginOfEvent().isBefore(this.beginOfExamPeriodDatePicker.getValue().atStartOfDay()) || event.getEndOfEvent().isAfter(this.endOfSuppExamPeriodDatePicker.getValue().plusDays(1).atStartOfDay())) {
                        this.eventDAO.deleteEvent(event);
                    }
                }
            } else {
                modalStage = new Stage();
                modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "The changes are saved.");
                setAndShowModalWindow(modalWindow);
            }

            App.user.setMaxPossibleExam(this.maxExamComboBox.getValue());
            App.user.setMaxPossibleExamPerSemester(this.maxSemComboBox.getValue());
            App.user.setBeginOfExamPeriod(this.beginOfExamPeriodDatePicker.getValue());
            App.user.setEndOfExamPeriod(this.endOfExamPeriodDatePicker.getValue());
            App.user.setBeginOfSupplementaryExamPeriod(this.beginOfSuppExamPeriodDatePicker.getValue());
            App.user.setEndOfSupplementaryExamPeriod(this.endOfSuppExamPeriodDatePicker.getValue());
            App.user.setDurationOfDailyActivities(this.durationOfDailyActivitiesComboBox.getValue());
            this.userDAO.updateUser(user);
            this.userDAO.close();
            this.subjectDAO.close();

            setAndShowStageAndScene(App.stage, "Main menu", "main_menu_window");
        }

    }

    public void backButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.userDAO.close();
        setAndShowStageAndScene(App.stage, "Main menu", "main_menu_window");
    }


    public void deleteProfileButtonClicked(ActionEvent actionEvent) throws SQLException, IOException {
        this.userDAO.close();
        App.modalStage = new Stage();
        modalWindow = new ModalWindow(modalStage, "delete_profile_window", "Login", "login_window", "");
        setAndShowModalWindow(modalWindow);
    }

    private boolean checkPasswords() {

        this.currentPasswordField.setStyle(null);
        this.newPasswordField.setStyle(null);
        this.newPasswordAgainField.setStyle(null);

        boolean currentPassword = true;

        if (this.currentPasswordField.getText().isEmpty()) {
            this.currentPasswordField.setStyle(BORDER_COLOR_RED);
            this.currentPasswordField.setText("");
            this.currentPasswordField.setPromptText("empty password");
            currentPassword = false;
        } else {
            if (!isThePasswordCorrect(this.currentPasswordField.getText(), App.user.getPassword())) {
                this.currentPasswordField.setText("");
                this.currentPasswordField.setPromptText("wrong password");
                this.currentPasswordField.setStyle(BORDER_COLOR_RED);
                this.newPasswordField.setText("");
                this.newPasswordAgainField.setText("");
                currentPassword = false;
            } else {
                this.currentPasswordField.setStyle(BORDER_COLOR_GREEN);
            }
        }

        if (currentPassword && this.newPasswordField.getText().isEmpty() && newPasswordAgainField.getText().isEmpty()) {
            return true;
        }


        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{7,15}$";
        boolean newPassword = false;
        boolean newPasswordAgain = false;

        if (Pattern.matches(passwordRegex, this.newPasswordField.getText()) && Pattern.matches(passwordRegex, this.newPasswordAgainField.getText())) {

            if (!this.newPasswordField.getText().equals(this.newPasswordAgainField.getText())) {
                this.newPasswordField.setStyle(TEXT_BOX_BORDER_COLOR_RED);
                this.newPasswordAgainField.setStyle(TEXT_BOX_BORDER_COLOR_RED);
                this.newPasswordField.setText("");
                this.newPasswordAgainField.setText("");
                this.newPasswordField.setPromptText("the given passwords are");
                this.newPasswordAgainField.setPromptText("different");
                newPassword = false;
                newPasswordAgain = false;
            } else {
                this.newPasswordField.setStyle(TEXT_BOX_BORDER_COLOR_GREEN);
                this.newPasswordAgainField.setStyle(TEXT_BOX_BORDER_COLOR_GREEN);
                newPassword = true;
                newPasswordAgain = true;
            }

        } else {

            newPassword = false;
            newPasswordAgain = false;

            if (Pattern.matches(passwordRegex, this.newPasswordField.getText())) {
                this.newPasswordField.setStyle(TEXT_BOX_BORDER_COLOR_GREEN);

            } else {
                if (this.newPasswordField.getText().isEmpty()) {
                    this.newPasswordField.setStyle(BORDER_COLOR_RED);
                    this.newPasswordField.setText("");
                    this.newPasswordField.setPromptText("empty password");
                } else {
                    this.newPasswordField.setStyle(BORDER_COLOR_RED);
                    this.newPasswordField.setText("");
                    this.newPasswordField.setPromptText("Format Error");
                }
            }

            if (Pattern.matches(passwordRegex, this.newPasswordAgainField.getText())) {
                this.newPasswordAgainField.setStyle(TEXT_BOX_BORDER_COLOR_GREEN);
            } else {
                if (this.newPasswordAgainField.getText().isEmpty()) {
                    this.newPasswordAgainField.setStyle(BORDER_COLOR_RED);
                    this.newPasswordAgainField.setText("");
                    this.newPasswordAgainField.setPromptText("empty password");
                } else {
                    this.newPasswordAgainField.setStyle(BORDER_COLOR_RED);
                    this.newPasswordAgainField.setText("");
                    this.newPasswordAgainField.setPromptText("Format Error");
                }

            }

        }

        if (this.newPasswordField.getText().equals(this.currentPasswordField.getText()) && !this.newPasswordField.getText().isEmpty() && !this.newPasswordAgainField.getText().isEmpty()) {
            this.newPasswordField.setStyle(BORDER_COLOR_RED);
            this.newPasswordField.setText("");
            this.newPasswordField.setPromptText("The old and new passwords");
            this.newPasswordAgainField.setStyle(BORDER_COLOR_RED);
            this.newPasswordAgainField.setText("");
            this.newPasswordAgainField.setPromptText(" are the same.");
            return false;
        }

        return currentPassword && newPassword && newPasswordAgain;

    }

    private boolean checkDate() {

        this.beginOfExamPeriodDatePicker.setStyle(null);
        this.endOfExamPeriodDatePicker.setStyle(null);
        this.beginOfSuppExamPeriodDatePicker.setStyle(null);
        this.endOfSuppExamPeriodDatePicker.setStyle(null);

        if (this.beginOfExamPeriodDatePicker.getValue().isBefore(this.endOfExamPeriodDatePicker.getValue())) {

            this.beginOfExamHintLabel.setStyle(MID_TEXT_COLOR_GREEN);
            this.endOfExamHintLabel.setStyle(MID_TEXT_COLOR_GREEN);
            this.beginOfExamPeriodDatePicker.setStyle(BORDER_COLOR_GREEN);
            this.endOfExamPeriodDatePicker.setStyle(BORDER_COLOR_GREEN);

            this.beginOfExamHintLabel.setText("OK");
            this.endOfExamHintLabel.setText("OK");

            if (this.endOfExamPeriodDatePicker.getValue().isBefore(this.beginOfSuppExamPeriodDatePicker.getValue())) {
                this.beginOfSuppExamHintLabel.setText("OK");
                this.beginOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_GREEN);
                this.beginOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_GREEN);
                if (this.beginOfSuppExamPeriodDatePicker.getValue().isBefore(this.endOfSuppExamPeriodDatePicker.getValue())) {
                    this.endOfSuppExamHintLabel.setText("OK");
                    this.endOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_GREEN);
                    this.endOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_GREEN);
                    return true;
                } else {
                    this.endOfSuppExamHintLabel.setText("ERROR");
                    this.endOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
                    this.endOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);
                }
            } else {
                this.beginOfSuppExamHintLabel.setText("ERROR");
                this.endOfSuppExamHintLabel.setText("ERROR");
                this.beginOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
                this.endOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
                this.beginOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);
                this.endOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);
            }

        } else {

            this.beginOfExamHintLabel.setText("ERROR");
            this.endOfExamHintLabel.setText("ERROR");
            this.beginOfSuppExamHintLabel.setText("ERROR");
            this.endOfSuppExamHintLabel.setText("ERROR");

            this.beginOfExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
            this.endOfExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
            this.beginOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
            this.endOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
            this.beginOfExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);
            this.endOfExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);
            this.beginOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);
            this.endOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);

        }

        return false;
    }

}

package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.UserDAO;
import org.thesis.dao.UserDAOImpl;
import org.thesis.model.User;
import org.thesis.utility.ModalWindow;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import static org.thesis.model.Event.BORDER_COLOR_GREEN;
import static org.thesis.model.Event.BORDER_COLOR_RED;
import static org.thesis.model.User.passwordHasher;
import static org.thesis.utility.ComboBoxInitializer.Type;
import static org.thesis.utility.ComboBoxInitializer.setComboBox;
import static org.thesis.utility.StageAndSceneInitializer.*;
import static org.thesis.utility.StageAndSceneInitializer.setAndShowModalWindow;

import static org.thesis.App.modalStage;

/**
 * This class controls the registration process.
 */
public class RegisterWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(RegisterWindowController.class);

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @FXML
    private ComboBox<Integer> maxSemComboBox;

    @FXML
    private ComboBox<Integer> maxExamComboBox;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField passwordAgainField;

    @FXML
    private DatePicker beginOfExamPeriodDatePicker;

    @FXML
    private DatePicker endOfExamPeriodDatePicker;

    @FXML
    private DatePicker beginOfSuppExamPeriodDatePicker;

    @FXML
    private DatePicker endOfSuppExamPeriodDatePicker;

    @FXML
    private ComboBox<Integer> learningTimePerDayComboBox;

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

    private UserDAO userDAO;
    private List<User> users;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.userDAO = new UserDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.users = userDAO.findAllUsers();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        saveButton.setGraphic(new ImageView("/icons/save.png"));
        Tooltip.install(this.saveButton, new Tooltip("Save"));
        backButton.setGraphic(new ImageView("/icons/back.png"));
        Tooltip.install(this.backButton, new Tooltip("Back"));

        this.usernameTextField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        this.passwordField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        this.passwordAgainField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);

        Tooltip.install(this.usernameTextField, new Tooltip("6-15 alphanumeric characters allowed."));
        Tooltip.install(this.passwordField, new Tooltip("7-20 character at least 1: a-z, 1: A-Z, 1: 0-9, and 1 spec. char. needed."));

        setToDefault();

        this.beginOfExamPeriodDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> endOfExamPeriodDatePicker.setValue(beginOfExamPeriodDatePicker.getValue().plusDays(1)));
        this.endOfExamPeriodDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> beginOfSuppExamPeriodDatePicker.setValue(endOfExamPeriodDatePicker.getValue().plusDays(1)));
        this.beginOfSuppExamPeriodDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> endOfSuppExamPeriodDatePicker.setValue(beginOfSuppExamPeriodDatePicker.getValue().plusDays(1)));
    }

    private void setToDefault() {

        setComboBox(maxSemComboBox, 1, 4, 2, Type.INTEGER.getValue());
        setComboBox(maxExamComboBox, 3, 9, 3, Type.INTEGER.getValue());
        setComboBox(durationOfDailyActivitiesComboBox, 10, 15, 2, Type.INTEGER.getValue());
        setComboBox(learningTimePerDayComboBox, 1, 12, 4, Type.INTEGER.getValue());

        this.beginOfExamPeriodDatePicker.setValue(LocalDateTime.now().toLocalDate());
        this.beginOfExamPeriodDatePicker.setEditable(false);
        this.endOfExamPeriodDatePicker.setValue(LocalDateTime.now().toLocalDate().plusDays(1));
        this.endOfExamPeriodDatePicker.setEditable(false);
        this.beginOfSuppExamPeriodDatePicker.setValue(LocalDateTime.now().toLocalDate().plusDays(2));
        this.beginOfSuppExamPeriodDatePicker.setEditable(false);
        this.endOfSuppExamPeriodDatePicker.setValue(LocalDateTime.now().toLocalDate().plusDays(3));
        this.endOfSuppExamPeriodDatePicker.setEditable(false);

    }


    public void registerButtonClicked(ActionEvent actionEvent) throws SQLException, IOException {

        if (checkGivenData()) {

            User user = new User();
            user.setUsername(this.usernameTextField.getText());
            user.setPassword(passwordHasher(this.passwordField.getText()));
            user.setMaxPossibleExam(this.maxExamComboBox.getValue());
            user.setMaxPossibleExamPerSemester(this.maxSemComboBox.getValue());
            user.setBeginOfExamPeriod(this.beginOfExamPeriodDatePicker.getValue());
            user.setEndOfExamPeriod(this.endOfExamPeriodDatePicker.getValue());
            user.setBeginOfSupplementaryExamPeriod(this.beginOfSuppExamPeriodDatePicker.getValue());
            user.setEndOfSupplementaryExamPeriod(this.endOfSuppExamPeriodDatePicker.getValue());
            user.setDurationOfDailyActivities(this.durationOfDailyActivitiesComboBox.getValue());
            user.setLearningTimePerDay(this.learningTimePerDayComboBox.getValue());
            user.setRestDayOnSaturday(false);
            user.setRestDayOnSunday(true);

            this.userDAO.insertUser(user);

            this.userDAO.close();

            modalStage = new Stage();
            modalWindow = new ModalWindow(App.modalStage, "message_window", "Login", "login_window", "Registration is successful!");
            setAndShowModalWindow(modalWindow);
        }

    }

    private boolean checkUserExist(String username) {
        for (int i = 0; i < this.users.size(); i++) {
            if (this.users.get(i).getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkGivenData() {
        boolean result = false;


        if (checkUserExist(this.usernameTextField.getText())) {
            this.usernameTextField.setText("");
            this.usernameTextField.setPromptText("user exist");
            this.usernameTextField.setStyle("-fx-text-box-border: #ff0000;");
            this.passwordField.setText("");
            this.passwordAgainField.setText("");
            setToDefault();
        } else {
            String userRegex = "^[a-zA-Z0-9]{6,15}$";
            String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{7,15}$";
            boolean user = false;
            boolean pass = false;
            boolean passAgain = false;
            if (Pattern.matches(userRegex, this.usernameTextField.getText())) {
                this.usernameTextField.setStyle("-fx-text-box-border: #00ff00; ");
                user = true;
            } else {
                if (this.usernameTextField.getText().isEmpty()) {
                    this.usernameTextField.setStyle("-fx-text-box-border: #ff0000;");
                    this.usernameTextField.setText("");
                    this.usernameTextField.setPromptText("empty username");
                } else {
                    this.usernameTextField.setStyle("-fx-text-box-border: #ff0000;");
                    this.usernameTextField.setText("");
                    this.usernameTextField.setPromptText("Format Error");
                }
                user = false;
            }

            if (Pattern.matches(passwordRegex, this.passwordField.getText()) && Pattern.matches(passwordRegex, this.passwordAgainField.getText())) {

                if (!this.passwordField.getText().equals(this.passwordAgainField.getText())) {
                    this.passwordField.setStyle("-fx-text-box-border: #ff0000;");
                    this.passwordAgainField.setStyle("-fx-text-box-border: #ff0000;");
                    this.passwordField.setText("");
                    this.passwordAgainField.setText("");
                    this.passwordField.setPromptText("the given passwords are");
                    this.passwordAgainField.setPromptText("different");
                    pass = false;
                    passAgain = false;
                } else {
                    this.passwordField.setStyle("-fx-text-box-border: #00ff00;");
                    this.passwordAgainField.setStyle("-fx-text-box-border: #00ff00;");
                    pass = true;
                    passAgain = true;
                }

            } else {

                if (Pattern.matches(passwordRegex, this.passwordField.getText())) {
                    this.passwordField.setStyle("-fx-text-box-border: #00ff00;");
                    pass = true;
                } else {
                    if (this.passwordField.getText().isEmpty()) {
                        this.passwordField.setStyle("-fx-text-box-border: #ff0000;");
                        this.passwordField.setText("");
                        this.passwordField.setPromptText("empty password");
                    } else {
                        this.passwordField.setStyle("-fx-text-box-border: #ff0000;");
                        this.passwordField.setText("");
                        this.passwordField.setPromptText("Format Error");
                    }
                    pass = false;
                }


                if (Pattern.matches(passwordRegex, this.passwordAgainField.getText())) {
                    this.passwordAgainField.setStyle("-fx-text-box-border: #00ff00;");
                    passAgain = true;
                } else {
                    if (this.passwordAgainField.getText().isEmpty()) {
                        this.passwordAgainField.setStyle("-fx-text-box-border: #ff0000;");
                        this.passwordAgainField.setText("");
                        this.passwordAgainField.setPromptText("empty password");
                    } else {
                        this.passwordAgainField.setStyle("-fx-text-box-border: #ff0000;");
                        this.passwordAgainField.setText("");
                        this.passwordAgainField.setPromptText("Format Error");
                    }
                    passAgain = false;
                }

            }

            result = user && pass && passAgain && checkExams() && checkDate();

        }

        return result;

    }

    private boolean checkExams() {
        if (this.maxSemComboBox.getValue() > this.maxExamComboBox.getValue()) {
            this.maxExamComboBox.setStyle(BORDER_COLOR_RED);
            this.maxSemComboBox.setStyle(BORDER_COLOR_RED);
            return false;
        }
        this.maxExamComboBox.setStyle(BORDER_COLOR_GREEN);
        this.maxSemComboBox.setStyle(BORDER_COLOR_GREEN);

        return true;
    }

    public void backButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.userDAO.close();
        setAndShowStageAndScene(App.stage, "Login", "login_window");
    }


    private boolean checkDate() {

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
                    this.endOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_GREEN);
                    return true;
                } else {
                    this.endOfSuppExamHintLabel.setText("ERROR");
                    this.endOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
                    this.endOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);

                    return false;
                }

            } else {
                this.beginOfSuppExamHintLabel.setText("ERROR");
                this.endOfSuppExamHintLabel.setText("ERROR");
                this.beginOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
                this.endOfSuppExamHintLabel.setStyle(MID_TEXT_COLOR_RED);
                this.beginOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);
                this.endOfSuppExamPeriodDatePicker.setStyle(BORDER_COLOR_RED);

                return false;
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

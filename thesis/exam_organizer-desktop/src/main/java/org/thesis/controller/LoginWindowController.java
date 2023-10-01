package org.thesis.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.UserDAO;
import org.thesis.dao.UserDAOImpl;
import org.thesis.model.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import static org.thesis.model.User.isThePasswordCorrect;
import static org.thesis.utility.StageAndSceneInitializer.*;

/**
 * This class controls the events of the opening window.
 */
public class LoginWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(LoginWindowController.class);

    @FXML
    private Button registerButton;

    @FXML
    private Button exitButton;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label orLabel;

    private UserDAO<User> userDAO;
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

        try {
            this.userDAO.close();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        this.loginButton.setGraphic(new ImageView("/icons/enter.png"));
        Tooltip.install(this.loginButton, new Tooltip("Login"));
        this.registerButton.setGraphic(new ImageView("/icons/add_user.png"));
        Tooltip.install(this.registerButton, new Tooltip("Register"));
        this.exitButton.setGraphic(new ImageView("/icons/exit.png"));
        Tooltip.install(this.exitButton, new Tooltip("Exit"));

        this.usernameTextField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        this.passwordField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);

        this.orLabel.setVisible(false);

        Platform.runLater(() -> this.usernameTextField.requestFocus());

        this.usernameTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                passwordField.requestFocus();
            }
        });

        this.passwordField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                loginButton.fire();
            }
        });

    }

    public void loginButtonClicked(ActionEvent actionEvent) {

        boolean user = true;
        boolean pass = true;

        if (this.usernameTextField.getText().trim().isEmpty()) {
            this.usernameTextField.setStyle(TEXT_BOX_BORDER_COLOR_RED);
            this.usernameTextField.setPromptText("Enter the username!");
            user = false;
        } else{
            this.usernameTextField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        }

        if (this.passwordField.getText().trim().isEmpty()) {
            this.passwordField.setStyle(TEXT_BOX_BORDER_COLOR_RED);
            this.passwordField.setPromptText("Enter the password!");
            pass = false;
        }else{
            this.passwordField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        }

        if (user && pass && checkUsernameAndPassword(this.usernameTextField.getText(), this.passwordField.getText())) {
            setAndShowStageAndScene(App.stage, "Main menu", "main_menu_window");
        }

    }

    private boolean checkUsernameAndPassword(String username, String password) {

        for (int i = 0; i < this.users.size(); i++) {
            if (this.users.get(i).getUsername().equals(username) && isThePasswordCorrect(password, this.users.get(i).getPassword())) {
                App.user.setId(this.users.get(i).getId());
                App.user.setUsername(this.users.get(i).getUsername());
                App.user.setPassword(this.users.get(i).getPassword());
                App.user.setMaxPossibleExam(this.users.get(i).getMaxPossibleExam());
                App.user.setMaxPossibleExamPerSemester(this.users.get(i).getMaxPossibleExamPerSemester());
                App.user.setBeginOfExamPeriod(this.users.get(i).getBeginOfExamPeriod());
                App.user.setEndOfExamPeriod(this.users.get(i).getEndOfExamPeriod());
                App.user.setBeginOfSupplementaryExamPeriod(this.users.get(i).getBeginOfSupplementaryExamPeriod());
                App.user.setEndOfSupplementaryExamPeriod(this.users.get(i).getEndOfSupplementaryExamPeriod());
                App.user.setDurationOfDailyActivities(this.users.get(i).getDurationOfDailyActivities());
                App.user.setLearningTimePerDay(this.users.get(i).getLearningTimePerDay());
                App.user.setRestDayOnSaturday(this.users.get(i).isRestDayOnSaturday());
                App.user.setRestDayOnSunday(this.users.get(i).isRestDayOnSunday());
                return true;
            }
        }

        this.usernameTextField.setStyle(TEXT_BOX_BORDER_COLOR_RED);
        this.usernameTextField.setText("");
        this.usernameTextField.setPromptText("wrong username");
        this.orLabel.setVisible(true);
        this.passwordField.setStyle(TEXT_BOX_BORDER_COLOR_RED);
        this.passwordField.setText("");
        this.passwordField.setPromptText("password");

        this.loginButton.requestFocus();

        return false;

    }

    public void registerButtonClicked(ActionEvent actionEvent) {
        setAndShowStageAndScene(App.stage, "Registration", "register_window");
    }

    public void exitButtonClicked(ActionEvent actionEvent) {
        Platform.exit();
    }

    public void usernameTextFieldClicked(MouseEvent mouseEvent) {
        this.usernameTextField.setPromptText("");
        this.usernameTextField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        this.passwordField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        this.orLabel.setVisible(false);
    }

    public void passwordTextFieldClicked(MouseEvent mouseEvent) {
        this.passwordField.setPromptText("");
        this.passwordField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        this.usernameTextField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);
        this.orLabel.setVisible(false);
    }

}

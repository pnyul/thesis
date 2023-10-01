package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.*;
import org.thesis.model.Event;
import org.thesis.model.Subject;
import org.thesis.model.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.thesis.App.modalStage;
import static org.thesis.model.User.isThePasswordCorrect;
import static org.thesis.utility.StageAndSceneInitializer.*;

/**
 * The methods of this class can be used to delete a user.
 */
public class DeleteProfileWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(DeleteProfileWindowController.class);

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    @FXML
    private PasswordField passwordField;

    private UserDAO<User> userDAO;
    private EventDAO<Event> eventDAO;
    private SubjectDAO<Subject> subjectDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.userDAO = new UserDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.eventDAO = new EventDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.subjectDAO = new SubjectDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        yesButton.setGraphic(new ImageView("/icons/checkmark.png"));
        Tooltip.install(this.yesButton, new Tooltip("Yes"));
        noButton.setGraphic(new ImageView("/icons/cancel.png"));
        Tooltip.install(this.noButton, new Tooltip("No"));

    }


    public void okButtonClicked(ActionEvent actionEvent) throws SQLException {

        if (isThePasswordCorrect(this.passwordField.getText(), App.user.getPassword())) {

            this.userDAO.deleteUser(App.user);
            this.subjectDAO.deleteAllSubjects(App.user);
            this.eventDAO.deleteAllEvents(App.user);
            this.userDAO.close();
            this.subjectDAO.close();
            this.eventDAO.close();

            modalStage.close();
            setAndShowStageAndScene(App.stage, modalWindow.getNewTitle(), modalWindow.getNewFxml());

        } else{
            this.passwordField.setText("");
            this.passwordField.setPromptText("wrong password");
            this.passwordField.setStyle("-fx-text-box-border: #ff0000;");
        }

    }

    public void backButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.userDAO.close();
        this.subjectDAO.close();
        this.eventDAO.close();
        modalStage.close();
        setAndShowStageAndScene(App.stage,"Settings", "settings_window");
    }

    public void passwordFieldClicked(ActionEvent actionEvent) {
        this.passwordField.setPromptText("");
        this.passwordField.setStyle(null);
    }
}

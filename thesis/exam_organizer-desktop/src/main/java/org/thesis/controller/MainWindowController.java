package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.EventDAO;
import org.thesis.dao.EventDAOImpl;
import org.thesis.dao.SubjectDAO;
import org.thesis.dao.SubjectDAOImpl;
import org.thesis.model.Event;
import org.thesis.model.Subject;
import org.thesis.model.User;
import org.thesis.utility.ModalWindow;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import static org.thesis.utility.StageAndSceneInitializer.*;
import static org.thesis.utility.StageAndSceneInitializer.setAndShowModalWindow;

/**
 * This class controls events in the main menu. For example, it redirects to the calendar.
 */
public class MainWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(MainWindowController.class);

    private SubjectDAO subjectDAO;
    private List<Subject> subjectsList;
    private EventDAO eventDAO;
    private List<Event> eventsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.subjectDAO = new SubjectDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.subjectsList = this.subjectDAO.findAllSubjects(App.user.getUsername());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.subjectDAO.close();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.eventDAO = new EventDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.eventsList = this.eventDAO.findAllEvents(App.user.getUsername(), Event.EventType.EXAM.getValue());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        for (Event event : this.eventsList) {
            if (event.getEndOfEvent().isBefore(LocalDateTime.now())) {
                try {
                    this.eventDAO.deleteEvent(event);
                } catch (SQLException e) {
                    logger.log(Level.ERROR, e);
                }
            }
        }

        try {
            this.eventDAO.close();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

    }


    public void subjectsButtonClicked(ActionEvent actionEvent) {
        setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");
    }

    public void eventsButtonClicked(ActionEvent actionEvent) {
        setAndShowStageAndScene(App.stage, "General events", "general_events_window");
    }

    public void settingsButtonClicked(ActionEvent actionEvent) {
        setAndShowStageAndScene(App.stage, "Settings", "settings_window");
    }

    public void logOutButtonClicked(ActionEvent actionEvent) {
        App.user = new User();
        setAndShowStageAndScene(App.stage, "Login", "login_window");
    }

    public void calendarButtonClicked(ActionEvent actionEvent) {

        if (subjectsList.isEmpty()) {
            App.modalStage = new Stage();
            modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "There is no subject added.");
            setAndShowModalWindow(modalWindow);
            setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");
        } else if (eventsList.isEmpty()) {
            App.modalStage = new Stage();
            modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "There is no exam date added.");
            setAndShowModalWindow(modalWindow);
            setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");
        } else {
            setAndShowStageAndScene(App.stage, "Calendar", "calendar_window");
        }

    }

}

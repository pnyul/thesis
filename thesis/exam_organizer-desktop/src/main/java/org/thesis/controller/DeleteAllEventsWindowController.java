package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.EventDAO;
import org.thesis.dao.EventDAOImpl;
import org.thesis.model.Event;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import static org.thesis.App.*;
import static org.thesis.utility.StageAndSceneInitializer.*;

/**
 * The methods of this class can be used to clear all events for a given user.
 */

public class DeleteAllEventsWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(DeleteAllEventsWindowController.class);

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    private EventDAO eventDAO;
    private List<Event> events;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.eventDAO = new EventDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.events = this.eventDAO.findAllEvents(App.user.getUsername(), modalWindow.getModalParameter());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        yesButton.setGraphic(new ImageView("/icons/checkmark.png"));
        Tooltip.install(this.yesButton, new Tooltip("Yes"));
        noButton.setGraphic(new ImageView("/icons/cancel.png"));
        Tooltip.install(this.noButton, new Tooltip("No"));

    }


    public void yesButtonClicked(ActionEvent actionEvent) throws SQLException {
        for (Event event : this.events) {
            this.eventDAO.deleteEvent(event);
        }

        backToGeneralEvents();
    }

    public void noButtonClicked(ActionEvent actionEvent) throws SQLException {
        backToGeneralEvents();
    }

    private void backToGeneralEvents() throws SQLException {
        eventDAO.close();
        modalStage.close();
        setAndShowStageAndScene(App.stage, modalWindow.getNewTitle(), modalWindow.getNewFxml());
    }

}

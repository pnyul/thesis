package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.EventDAO;
import org.thesis.dao.EventDAOImpl;
import org.thesis.model.Event;
import org.thesis.utility.ModalWindow;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

import static org.thesis.model.Event.*;
import static org.thesis.utility.ComboBoxInitializer.Type;
import static org.thesis.utility.ComboBoxInitializer.setComboBox;
import static org.thesis.utility.StageAndSceneInitializer.*;

/**
 * Methods of this class adds exams after checking the given inputs.
 */
public class AddExamEventWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(AddExamEventWindowController.class);

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    @FXML
    private DatePicker beginOfExamDatePicker;

    @FXML
    private DatePicker endOfExamDatePicker;

    @FXML
    private ComboBox<String> beginHoursComboBox;

    @FXML
    private ComboBox<String> beginMinutesComboBox;

    @FXML
    private ComboBox<String> endHoursComboBox;

    @FXML
    private ComboBox<String> endMinutesComboBox;

    private EventDAO eventDAO;
    private Event examEvent;
    private List<Event> exams;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.eventDAO = new EventDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.exams = this.eventDAO.findAllEvents(App.user.getUsername(), EventType.EXAM.getValue());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        addButton.setGraphic(new ImageView("/icons/add_event.png"));
        Tooltip.install(this.addButton, new Tooltip("Add exam"));
        backButton.setGraphic(new ImageView("/icons/back.png"));
        Tooltip.install(this.addButton, new Tooltip("back"));

        this.examEvent = new Event();

        LocalDate dateForDatePicker;
        dateForDatePicker = LocalDate.now().isBefore(App.user.getBeginOfExamPeriod()) ? App.user.getBeginOfExamPeriod() : LocalDate.now();

        this.beginOfExamDatePicker.setValue(dateForDatePicker);
        this.endOfExamDatePicker.setValue(dateForDatePicker);
        this.beginOfExamDatePicker.setEditable(false);
        this.endOfExamDatePicker.setEditable(false);

        setComboBox(beginHoursComboBox, 0, 23, 12, Type.TIME.getValue());
        setComboBox(beginMinutesComboBox, 0, 59, 0, Type.TIME.getValue());
        setComboBox(endHoursComboBox, 0, 23, 14, Type.TIME.getValue());
        setComboBox(endMinutesComboBox, 0, 59, 0, Type.TIME.getValue());

        this.beginOfExamDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> endOfExamDatePicker.setValue(beginOfExamDatePicker.getValue()));
        this.beginHoursComboBox.valueProperty().addListener((observableValue, localDate, t1) -> endHoursComboBox.getSelectionModel().select(Integer.parseInt(beginHoursComboBox.getValue())));

    }


    public void addNewExamButtonClicked(ActionEvent actionEvent) throws SQLException {

        if (!areTheDateAndTimeOfAOneTimeEventCorrect(this.beginOfExamDatePicker, this.endOfExamDatePicker, this.beginHoursComboBox, this.beginMinutesComboBox, this.endHoursComboBox, this.endMinutesComboBox)) {
            return;
        }

        String beginOfExamAsString = dateAndTimeToString(this.beginOfExamDatePicker, this.beginHoursComboBox, this.beginMinutesComboBox);
        String endOfExamAsString = dateAndTimeToString(this.endOfExamDatePicker, this.endHoursComboBox, this.endMinutesComboBox);
        LocalDateTime beginOfExam = localDateTimeParser(beginOfExamAsString, DATE_TIME_PATTERN);
        LocalDateTime endOfExam = localDateTimeParser(endOfExamAsString, DATE_TIME_PATTERN);

        if (isEventInThePast(beginOfExam)) {
            App.modalStage = new Stage();
            modalWindow = new ModalWindow(App.modalStage, "message_window", "Add " + SubjectsWindowController.examEvent.getNameOfEvent() + " exam date", "add_exam_event_window", "The event is in the past.");
            setAndShowModalWindow(modalWindow);
            return;
        }

        if (durationOfEventInMinutes(beginOfExam, endOfExam) > MAX_DURATION_OF_EXAM_EVENT_IN_MINUTE) {
            App.modalStage = new Stage();
            modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "This exam exceeds the allowed duration (" + MAX_DURATION_OF_EXAM_EVENT_IN_MINUTE / HOUR_TO_MINUTES + "h).");
            setAndShowModalWindow(modalWindow);
            return;
        }

        if (!isEventInTheExamPeriod(EventType.EXAM.getValue(), beginOfExam, endOfExam, App.user.getBeginOfExamPeriod(), App.user.getEndOfSupplementaryExamPeriod())) {
            App.modalStage = new Stage();
            modalWindow = new ModalWindow(App.modalStage, "message_window", "Add " + SubjectsWindowController.examEvent.getNameOfEvent() + " exam date", "add_exam_event_window", "The event is out of the exam period.");
            setAndShowModalWindow(modalWindow);
            return;
        }

        if (!isTheEventUnique(this.exams, SubjectsWindowController.examEvent.getNameOfEvent(), beginOfExam, endOfExam)) {
            App.modalStage = new Stage();
            modalWindow = new ModalWindow(App.modalStage, "message_window", "Add " + SubjectsWindowController.examEvent.getNameOfEvent() + " exam date", "add_exam_event_window", "This exam is already exists.");
            setAndShowModalWindow(modalWindow);
            return;
        }

        this.examEvent.setNameOfEvent(SubjectsWindowController.examEvent.getNameOfEvent());
        this.examEvent.setUsername(App.user.getUsername());
        this.examEvent.setBeginOfEvent(beginOfExam);
        this.examEvent.setEndOfEvent(endOfExam);
        this.examEvent.setType(EventType.EXAM.getValue());
        this.examEvent.setActive(true);

        this.eventDAO.insertEvent(examEvent);

        this.eventDAO.close();

        setAndShowStageAndScene(App.stage, SubjectsWindowController.examEvent.getNameOfEvent() + " exam dates", "exam_events_window");
    }

    public void backButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.eventDAO.close();
        setAndShowStageAndScene(App.stage, SubjectsWindowController.examEvent.getNameOfEvent() + " exam dates", "exam_events_window");
    }


}

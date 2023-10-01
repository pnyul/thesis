package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static org.thesis.utility.StageAndSceneInitializer.*;
import static org.thesis.model.Event.*;
import static org.thesis.utility.ComboBoxInitializer.*;
import static org.thesis.App.modalStage;

/**
 * Methods of this class adds exams after checking the given inputs.
 */
public class AddGeneralEventWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(AddGeneralEventWindowController.class);

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    @FXML
    private CheckBox dailyCheckBox;

    @FXML
    private ComboBox<String> beginHoursComboBox;

    @FXML
    private ComboBox<String> endMinutesComboBox;

    @FXML
    private ComboBox<String> endHoursComboBox;

    @FXML
    private ComboBox<String> beginMinutesComboBox;

    @FXML
    private TextField nameOfEventTextField;

    @FXML
    private DatePicker beginOfEventDatePicker;

    @FXML
    private DatePicker endOfEventDatePicker;

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
            this.events = this.eventDAO.findAllEvents(App.user.getUsername(), Event.EventType.GENERAL.getValue());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        addButton.setGraphic(new ImageView("/icons/add_event.png"));
        Tooltip.install(this.addButton, new Tooltip("Add general event"));
        backButton.setGraphic(new ImageView("/icons/back.png"));
        Tooltip.install(this.backButton, new Tooltip("Back"));

        this.nameOfEventTextField.setStyle(TEXT_BOX_BORDER_COLOR_BLACK);

        LocalDate dateForDatePicker;
        dateForDatePicker = LocalDate.now().isBefore(App.user.getBeginOfExamPeriod()) ? App.user.getBeginOfExamPeriod() : LocalDate.now();

        this.beginOfEventDatePicker.setValue(dateForDatePicker);
        this.endOfEventDatePicker.setValue(dateForDatePicker);
        this.beginOfEventDatePicker.setEditable(false);
        this.endOfEventDatePicker.setEditable(false);

        setComboBox(beginHoursComboBox, 0, 23, 12, Type.TIME.getValue());
        setComboBox(beginMinutesComboBox, 0, 59, 0, Type.TIME.getValue());
        setComboBox(endHoursComboBox, 0, 23, 14, Type.TIME.getValue());
        setComboBox(endMinutesComboBox, 0, 59, 0, Type.TIME.getValue());

        this.beginOfEventDatePicker.valueProperty().addListener((observableValue, localDate, t1) -> endOfEventDatePicker.setValue(beginOfEventDatePicker.getValue()));
        this.beginHoursComboBox.valueProperty().addListener((observableValue, localDate, t1) -> endHoursComboBox.getSelectionModel().select(Integer.parseInt(beginHoursComboBox.getValue())));

    }

    public void addNewEventButtonClicked(ActionEvent actionEvent) throws SQLException {

        if (Objects.equals(this.nameOfEventTextField.getText(), "")) {
            this.nameOfEventTextField.setStyle("-fx-text-box-border: #ff0000;");
            this.nameOfEventTextField.setPromptText("The event name is empty.");
            return;
        }

        Event event;

        if (this.dailyCheckBox.isSelected()) {

            if (!isTheTimeOfARegularEventCorrect(Integer.parseInt(this.beginHoursComboBox.getValue()), Integer.parseInt(this.beginMinutesComboBox.getValue()), Integer.parseInt(this.endHoursComboBox.getValue()), Integer.parseInt(this.endMinutesComboBox.getValue()))) {
                return;
            }

            LocalDateTime beginOfEvent;
            LocalDateTime endOfEvent;

            if (LocalDate.now().isBefore(App.user.getBeginOfExamPeriod().minusWeeks(1)) || LocalDate.now().isEqual(App.user.getBeginOfExamPeriod().minusWeeks(1))) {
                beginOfEvent = LocalDateTime.of(App.user.getBeginOfExamPeriod().minusWeeks(1), LocalTime.of(Integer.parseInt(this.beginHoursComboBox.getValue()), Integer.parseInt(this.beginMinutesComboBox.getValue())));
                endOfEvent = LocalDateTime.of(App.user.getBeginOfExamPeriod().minusWeeks(1), LocalTime.of(Integer.parseInt(this.endHoursComboBox.getValue()), Integer.parseInt(this.endMinutesComboBox.getValue())));
            } else {
                beginOfEvent = LocalDateTime.of(LocalDate.now(), LocalTime.of(Integer.parseInt(this.beginHoursComboBox.getValue()), Integer.parseInt(this.beginMinutesComboBox.getValue())));
                endOfEvent = LocalDateTime.of(LocalDate.now(), LocalTime.of(Integer.parseInt(this.endHoursComboBox.getValue()), Integer.parseInt(this.endMinutesComboBox.getValue())));
            }

            LocalDateTime lastDay = App.user.getEndOfSupplementaryExamPeriod().plusDays(1).atStartOfDay();

            if (durationOfEventInMinutes(beginOfEvent, endOfEvent) > MAX_DURATION_OF_GENERAL_EVENT_IN_MINUTE) {
                modalStage = new Stage();
                modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "The event exceeds the allowed duration.");
                setAndShowModalWindow(modalWindow);
                return;
            }

            int notUniqueEventCounter = 0;

            while (beginOfEvent.isBefore(lastDay)) {

                if (!isTheEventUnique(this.events, this.nameOfEventTextField.getText(), beginOfEvent, endOfEvent)) {

                    notUniqueEventCounter++;

                    beginOfEvent = beginOfEvent.plusDays(1);
                    endOfEvent = endOfEvent.plusDays(1);
                    continue;
                }

                event = new Event();

                event.setNameOfEvent(this.nameOfEventTextField.getText());
                event.setUsername(App.user.getUsername());
                event.setBeginOfEvent(beginOfEvent);
                event.setEndOfEvent(endOfEvent);
                event.setType(Event.EventType.GENERAL.getValue());
                event.setActive(areEventsColliding(this.events, beginOfEvent, endOfEvent));

                this.eventDAO.insertEvent(event);

                beginOfEvent = beginOfEvent.plusDays(1);
                endOfEvent = endOfEvent.plusDays(1);

            }

            if (notUniqueEventCounter > 0) {

                String message;

                long difference = ChronoUnit.DAYS.between(App.user.getBeginOfExamPeriod(), App.user.getEndOfSupplementaryExamPeriod()) + 1;

                message = "" + notUniqueEventCounter + " event" + (notUniqueEventCounter == 1 ? "" : "s") + " from " + difference + " already existed.";
                App.modalStage = new Stage();
                modalStage = new Stage();
                modalWindow = new ModalWindow(App.modalStage, "message_window", "Add general event", "add_general_event_window", message);
                setAndShowModalWindow(modalWindow);

            }

        } else {

            if (!areTheDateAndTimeOfAOneTimeEventCorrect(beginOfEventDatePicker, this.endOfEventDatePicker, this.beginHoursComboBox, this.beginMinutesComboBox, this.endHoursComboBox, this.endMinutesComboBox)) {
                return;
            }

            String beginOfEventAsString = dateAndTimeToString(this.beginOfEventDatePicker, this.beginHoursComboBox, this.beginMinutesComboBox);
            String endOfEventAsString = dateAndTimeToString(this.endOfEventDatePicker, this.endHoursComboBox, this.endMinutesComboBox);
            LocalDateTime beginOfEvent = localDateTimeParser(beginOfEventAsString, DATE_TIME_PATTERN);
            LocalDateTime endOfEvent = localDateTimeParser(endOfEventAsString, DATE_TIME_PATTERN);

            if (durationOfEventInMinutes(beginOfEvent, endOfEvent) > MAX_DURATION_OF_GENERAL_EVENT_IN_MINUTE) {
                App.modalStage = new Stage();
                modalStage = new Stage();
                modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "The event exceeds the allowed duration (" + MAX_DURATION_OF_GENERAL_EVENT_IN_MINUTE / HOUR_TO_MINUTES + "h).");
                setAndShowModalWindow(modalWindow);
                return;
            }

            if (isEventInThePast(beginOfEvent)) {
                App.modalStage = new Stage();
                modalStage = new Stage();
                modalWindow = new ModalWindow(App.modalStage, "message_window", "Add general event", "add_general_event_window", "The event is in the past.");
                setAndShowModalWindow(modalWindow);
                return;
            }

            if (!isEventInTheExamPeriod(EventType.GENERAL.getValue(), beginOfEvent, endOfEvent, App.user.getBeginOfExamPeriod(), App.user.getEndOfSupplementaryExamPeriod())) {
                App.modalStage = new Stage();
                modalStage = new Stage();
                modalWindow = new ModalWindow(App.modalStage, "message_window", "Add general event", "add_general_event_window", "The event is out of the exam period.");
                setAndShowModalWindow(modalWindow);
                return;
            }

            if (!isTheEventUnique(this.events, this.nameOfEventTextField.getText(), beginOfEvent, endOfEvent)) {
                App.modalStage = new Stage();
                modalStage = new Stage();
                modalWindow = new ModalWindow(App.modalStage, "message_window", "Add general event", "add_general_event_window", "This event already exists.");
                setAndShowModalWindow(modalWindow);
                return;
            }

            event = new Event();

            event.setNameOfEvent(this.nameOfEventTextField.getText());
            event.setUsername(App.user.getUsername());
            event.setBeginOfEvent(beginOfEvent);
            event.setEndOfEvent(endOfEvent);
            event.setType(Event.EventType.GENERAL.getValue());
            event.setActive(areEventsColliding(this.events, beginOfEvent, endOfEvent));

            this.eventDAO.insertEvent(event);

        }

        this.eventDAO.close();

        setAndShowStageAndScene(App.stage, "General events", "general_events_window");

    }

    public void backButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.eventDAO.close();
        setAndShowStageAndScene(App.stage, "General events", "general_events_window");
    }

    public void dailyCheckBoxOnAction(ActionEvent actionEvent) {
        if (this.dailyCheckBox.isSelected()) {
            this.beginOfEventDatePicker.setDisable(true);
            this.endOfEventDatePicker.setDisable(true);

        } else {
            this.beginOfEventDatePicker.setDisable(false);
            this.endOfEventDatePicker.setDisable(false);
        }
    }

    public void nameOfEventTextFieldClicked(MouseEvent mouseEvent) {
        this.nameOfEventTextField.setPromptText("");
        this.nameOfEventTextField.setStyle(null);
    }

}

package org.thesis.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.EventDAO;
import org.thesis.dao.EventDAOImpl;
import org.thesis.model.Event;
import org.thesis.utility.ModalWindow;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.thesis.utility.StageAndSceneInitializer.*;
import static org.thesis.utility.StageAndSceneInitializer.setAndShowModalWindow;

/**
 * This class handles general events for the user.
 */
public class GeneralEventWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(GeneralEventWindowController.class);

    @FXML
    private Button saveButton;

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    @FXML
    private Button deleteAllButton;

    @FXML
    private TableColumn<Event, String> nameOfEventColumn;

    @FXML
    private TableColumn<Event, LocalDateTime> beginOfEventColumn;

    @FXML
    private TableColumn<Event, LocalDateTime> endOfEventColumn;

    @FXML
    private TableColumn<Event, Boolean> activeColumn;

    @FXML
    private TableColumn<Event, Void> deleteColumn;

    @FXML
    private TableView<Event> generalEventsTableView;

    @FXML
    private CheckBox allCheckBox;

    private EventDAO<Event> eventDAO;
    private List<Event> events;
    private List<Event> eventsOriginal;
    private ObservableList<Event> eventsList;

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

        if (this.events.isEmpty()) {
            this.deleteAllButton.setVisible(false);
        }

        saveButton.setGraphic(new ImageView("/icons/save.png"));
        Tooltip.install(this.saveButton, new Tooltip("Save"));
        addButton.setGraphic(new ImageView("/icons/plus.png"));
        Tooltip.install(this.addButton, new Tooltip("Add new event"));
        backButton.setGraphic(new ImageView("/icons/back.png"));
        Tooltip.install(this.backButton, new Tooltip("Back"));
        deleteAllButton.setGraphic(new ImageView("/icons/delete_trash.png"));
        Tooltip.install(this.deleteAllButton, new Tooltip("Delete all events"));

        Comparator<Event> comparator = Comparator.comparing(Event::getBeginOfEvent);
        this.events.sort(comparator);

        this.eventsOriginal = new ArrayList<>();

        for (Event eventOrig : this.events) {

            Event event = new Event();

            event.setId(eventOrig.getId());
            event.setNameOfEvent(eventOrig.getNameOfEvent());
            event.setUsername(App.user.getUsername());
            event.setBeginOfEvent(eventOrig.getBeginOfEvent());
            event.setEndOfEvent(eventOrig.getEndOfEvent());
            event.setActive(eventOrig.isActive());
            event.setType(eventOrig.getType());

            this.eventsOriginal.add(event);

        }


        this.generalEventsTableView.getItems().setAll(this.events);

        int selected = 0;

        for (Event event : this.events) {
            if (event.isActive()) {
                selected++;
            }
        }

        if (this.events.isEmpty()) {
            this.allCheckBox.setSelected(false);
        } else {
            this.allCheckBox.setSelected(selected == this.events.size());
        }

        nameOfEventColumn.setCellValueFactory(new PropertyValueFactory<>("nameOfEvent"));
        beginOfEventColumn.setCellValueFactory(new PropertyValueFactory<>("beginOfEvent"));
        endOfEventColumn.setCellValueFactory(new PropertyValueFactory<>("EndOfEvent"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        beginOfEventColumn.setCellValueFactory(cellData -> cellData.getValue().beginOfEventProperty());

        beginOfEventColumn.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format(item.format(formatter)));
            }
        });

        endOfEventColumn.setCellValueFactory(cellData -> cellData.getValue().endOfEventProperty());

        endOfEventColumn.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format(item.format(formatter)));
            }
        });

        Callback<TableColumn<Event, Void>, TableCell<Event, Void>> deleteCellFactory = new Callback<>() {
            @Override
            public TableCell<Event, Void> call(final TableColumn<Event, Void> param) {
                final TableCell<Event, Void> cell = new TableCell<>() {

                    private final Button button = new Button("Delete");

                    {
                        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        button.setGraphic(new ImageView("/icons/delete_trash.png"));
                        button.setOnAction((ActionEvent event) -> {

                            Event data = getTableView().getItems().get(getIndex());

                            try {
                                eventDAO.deleteEvent(data);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            try {
                                eventDAO.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            setAndShowStageAndScene(App.stage, "Events", "general_events_window");

                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        if (isEmpty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        };


        this.deleteColumn.setCellFactory(deleteCellFactory);

        this.activeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Event, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Event, Boolean> value) {
                return value.getValue().activeProperty();
            }
        });

        activeColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activeColumn));

        this.generalEventsTableView.setEditable(true);

        allCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {

                eventsList = generalEventsTableView.getItems();

                if (allCheckBox.isSelected()) {
                    for (Event event : eventsList) {
                        event.setActive(true);
                    }
                } else {
                    for (Event event : eventsList) {
                        event.setActive(false);
                    }
                }

            }
        });


        this.generalEventsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    public void addNewEventButtonClicked(ActionEvent actionEvent) {
        setAndShowStageAndScene(App.stage, "Add general event(s)", "add_general_event_window");
    }

    public void backButtonClicked(ActionEvent actionEvent) {
        setAndShowStageAndScene(App.stage, "Main menu", "main_menu_window");
    }

    public void saveButtonClicked(ActionEvent actionEvent) throws SQLException, IOException {

        this.generalEventsTableView.getSelectionModel().clearSelection();

        if (checkFullCollision()) {
            App.modalStage = new Stage();
            modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "Several events collide. Marked in yellow.");
            setAndShowModalWindow(modalWindow);
            return;
        }

        if (!checkFreeTime()) {
            App.modalStage = new Stage();
            modalWindow = new ModalWindow(App.modalStage, "message_window", "", "", "Too many events for a given day. Marked in red.");
            setAndShowModalWindow(modalWindow);
            return;
        }


        this.generalEventsTableView.setStyle("-fx-selection-bar: yellow");

        Event event;

        for (int i = 0; i < this.eventsOriginal.size(); i++) {

            if (this.eventsOriginal.get(i).isActive() != this.events.get(i).isActive()) {

                event = new Event();

                event.setId(this.events.get(i).getId());
                event.setNameOfEvent(this.events.get(i).getNameOfEvent());
                event.setUsername(this.events.get(i).getUsername());
                event.setBeginOfEvent(this.events.get(i).getBeginOfEvent());
                event.setEndOfEvent(this.events.get(i).getEndOfEvent());
                event.setActive(this.events.get(i).isActive());
                event.setType(this.events.get(i).getType());
                event.setActive(this.events.get(i).isActive());

                this.eventDAO.updateEvent(event);
            }

        }

        this.eventDAO.close();
        setAndShowStageAndScene(App.stage, "Main menu", "main_menu_window");

    }

    public boolean checkFullCollision() {

        this.generalEventsTableView.setStyle("-fx-selection-bar-non-focused: yellow");
        this.generalEventsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        boolean collisionExist = false;

        for (int i = 0; i < this.events.size() - 1; i++) {

            if (!this.events.get(i).isActive()) {
                continue;
            }

            for (int j = i + 1; j < this.events.size(); j++) {

                if (this.events.get(j).isActive() && ((this.events.get(i).getBeginOfEvent().isBefore(this.events.get(j).getBeginOfEvent()) || this.events.get(i).getBeginOfEvent().isEqual(this.events.get(j).getBeginOfEvent())) && this.events.get(i).getEndOfEvent().isAfter(this.events.get(j).getBeginOfEvent())) || (this.events.get(i).getBeginOfEvent().isAfter(this.events.get(j).getBeginOfEvent()) && this.events.get(i).getBeginOfEvent().isBefore(this.events.get(j).getEndOfEvent()))) {

                    if (!this.generalEventsTableView.getSelectionModel().isSelected(i)) {
                        this.generalEventsTableView.getSelectionModel().select(i);
                    }

                    this.generalEventsTableView.getSelectionModel().select(j);

                    collisionExist = true;
                }

            }
        }

        return collisionExist;
    }

    private boolean checkFreeTime() {

        this.generalEventsTableView.setStyle("-fx-selection-bar-non-focused: red");
        this.generalEventsTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        LocalDateTime current = this.events.get(0).getBeginOfEvent().withHour(00).withMinute(00);
        LocalDateTime last = this.events.get(this.events.size() - 1).getBeginOfEvent().withHour(23).withMinute(59);

        Set<LocalDate> dates = new HashSet<>();

        int timeLeft = Event.MINUTES_IN_A_DAY - App.user.getDurationOfDailyActivities() * Event.HOUR_TO_MINUTES - App.user.getLearningTimePerDay() * Event.HOUR_TO_MINUTES;

        int freeMinutesOnTheGivenDay = timeLeft;

        while (current.isBefore(last)) {

            if ((current.getDayOfWeek() == DayOfWeek.SATURDAY && App.user.isRestDayOnSaturday()) || (current.getDayOfWeek() == DayOfWeek.SUNDAY && App.user.isRestDayOnSunday())) {
                freeMinutesOnTheGivenDay += App.user.getLearningTimePerDay() * Event.HOUR_TO_MINUTES;
            }

            for (Event event : this.events) {

                if (event.isActive()) {

                    if ((event.getBeginOfEvent().isAfter(current) || event.getBeginOfEvent().isEqual(current)) && (event.getEndOfEvent().isBefore(current.plusDays(1)) || event.getEndOfEvent().isEqual(current.plusDays(1)))) {
                        freeMinutesOnTheGivenDay -= (int) Duration.between(event.getBeginOfEvent(), event.getEndOfEvent()).toMinutes();
                    }

                    if (event.getBeginOfEvent().isBefore(current) && event.getEndOfEvent().isAfter(current)) {
                        freeMinutesOnTheGivenDay -= (int) Duration.between(current, event.getEndOfEvent()).toMinutes();
                    }

                    if (event.getBeginOfEvent().isBefore(current.plusDays(1)) && event.getEndOfEvent().isAfter(current.plusDays(1))) {
                        freeMinutesOnTheGivenDay -= (int) Duration.between(event.getBeginOfEvent(), current.plusDays(1)).toMinutes();
                    }

                }

                if (freeMinutesOnTheGivenDay < 0) {
                    dates.add(current.toLocalDate());
                }

            }

            freeMinutesOnTheGivenDay = timeLeft;
            current = current.plusDays(1);
        }

        if (!dates.isEmpty()) {

            for (int i = 0; i < this.generalEventsTableView.getItems().size(); i++) {
                if (dates.contains(this.generalEventsTableView.getItems().get(i).getBeginOfEvent().toLocalDate())) {
                    this.generalEventsTableView.getSelectionModel().select(i);
                }
            }

            return false;
        }


        return true;
    }

    public void deleteAllButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.eventDAO.close();
        App.modalStage = new Stage();
        modalWindow = new ModalWindow(App.modalStage, "delete_all_events_window", "General events", "general_events_window", "", Event.EventType.GENERAL.getValue());
        setAndShowModalWindow(modalWindow);
    }


}
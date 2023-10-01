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
import org.thesis.dao.SubjectDAO;
import org.thesis.dao.SubjectDAOImpl;
import org.thesis.model.Event;
import org.thesis.model.Subject;
import org.thesis.utility.ModalWindow;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static org.thesis.model.Event.DATE_TIME_PATTERN;
import static org.thesis.utility.StageAndSceneInitializer.*;


/**
 * This class lists and manages the user's exams to be displayed.
 */
public class ExamEventWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(ExamEventWindowController.class);

    @FXML
    private Button backButton;

    @FXML
    private Button addButton;

    @FXML
    private Button saveButton;

    @FXML
    private CheckBox allCheckBox;

    @FXML
    private Button deleteAllButton;

    @FXML
    private TableView<Event> examDatesTableView;

    @FXML
    private TableColumn<Event, LocalDateTime> beginOfExamColumn;

    @FXML
    private TableColumn<Event, LocalDateTime> endOfExamColumn;

    @FXML
    private TableColumn<Event, Boolean> activeColumn;

    @FXML
    private TableColumn<Event, Void> deleteColumn;

    private EventDAO eventDAO;
    private List<Event> exams;
    private List<Event> examsOriginal;
    private SubjectDAO subjectDAO;
    private List<Subject> subjects;
    private ObservableList<Event> examList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.eventDAO = new EventDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.exams = this.eventDAO.findAllEvents(App.user.getUsername(), Event.EventType.EXAM.getValue());
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

        saveButton.setGraphic(new ImageView("/icons/save.png"));
        Tooltip.install(this.saveButton, new Tooltip("Save"));
        addButton.setGraphic(new ImageView("/icons/plus.png"));
        Tooltip.install(this.addButton, new Tooltip("Add new exam date"));
        backButton.setGraphic(new ImageView("/icons/back.png"));
        Tooltip.install(this.backButton, new Tooltip("Back"));
        deleteAllButton.setGraphic(new ImageView("/icons/delete_trash.png"));
        Tooltip.install(this.deleteAllButton, new Tooltip("Delete all exam dates"));

        for (Subject subject : this.subjects) {
            if (subject.isSuccess()) {
                for (Event exam : this.exams) {
                    if (subject.getSubject().equals(exam.getNameOfEvent())) {
                        exam.setActive(false);
                    }
                }
            }
        }


        Comparator<Event> comparator = Comparator.comparing(Event::getBeginOfEvent);
        this.exams.sort(comparator);

        this.exams.removeIf(exam -> !exam.getNameOfEvent().equals(SubjectsWindowController.examEvent.getNameOfEvent()));

        if (this.exams.isEmpty()) {
            this.deleteAllButton.setVisible(false);
        }

        this.examsOriginal = new ArrayList<>();

        for (int i = 0; i < this.exams.size(); i++) {

            Event exam = new Event();

            exam.setId(this.exams.get(i).getId());
            exam.setNameOfEvent(this.exams.get(i).getNameOfEvent());
            exam.setUsername(this.exams.get(i).getUsername());
            exam.setBeginOfEvent(this.exams.get(i).getBeginOfEvent());
            exam.setEndOfEvent(this.exams.get(i).getEndOfEvent());
            exam.setType(Event.EventType.EXAM.getValue());
            exam.setActive(this.exams.get(i).isActive());

            this.examsOriginal.add(exam);
        }

        this.examDatesTableView.getItems().setAll(this.exams);
        this.examDatesTableView.setEditable(true);

        int selected = 0;

        for (Event exam : this.exams) {
            if (exam.isActive()) {
                selected++;
            }
        }

        if (this.exams.isEmpty()) {
            this.allCheckBox.setSelected(false);
        } else {
            this.allCheckBox.setSelected(selected == this.exams.size());
        }

        beginOfExamColumn.setCellValueFactory(new PropertyValueFactory<>("beginOfEvent"));
        endOfExamColumn.setCellValueFactory(new PropertyValueFactory<>("endOfEvent"));

        beginOfExamColumn.setCellValueFactory(cellData -> cellData.getValue().beginOfEventProperty());

        beginOfExamColumn.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format(item.format(formatter)));
            }
        });

        endOfExamColumn.setCellValueFactory(cellData -> cellData.getValue().endOfEventProperty());

        endOfExamColumn.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format(item.format(formatter)));
            }
        });

        this.activeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Event, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Event, Boolean> value) {
                return value.getValue().activeProperty();
            }
        });

        activeColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activeColumn));


        Callback<TableColumn<Event, Void>, TableCell<Event, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Event, Void> call(final TableColumn<Event, Void> param) {
                final TableCell<Event, Void> cell = new TableCell<>() {

                    private final Button button = new Button("Delete");

                    {
                        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        button.setGraphic(new ImageView("/icons/delete_trash.png"));
                        Tooltip.install(this.button, new Tooltip("Delete exam date"));
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

                            setAndShowStageAndScene(App.stage, "Exam events", "exam_events_window");

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


        this.deleteColumn.setCellFactory(cellFactory);

        allCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {

                examList = examDatesTableView.getItems();

                if (allCheckBox.isSelected()) {
                    for (Event event : examList) {
                        event.setActive(true);
                    }
                } else {
                    for (Event event : examList) {
                        event.setActive(false);
                    }
                }

            }
        });


    }


    public void addNewExamButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.eventDAO.close();
        setAndShowStageAndScene(App.stage, "Add " + SubjectsWindowController.examEvent.getNameOfEvent() + " exam date", "add_exam_event_window");
    }

    public void backButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.eventDAO.close();
        setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");
    }


    public void activeOnEditCommit(TableColumn.CellEditEvent<Event, Boolean> eventBooleanCellEditEvent) {
    }

    public void saveButtonClicked(ActionEvent actionEvent) throws SQLException {

        for (int i = 0; i < this.examsOriginal.size(); i++) {
            if (this.exams.get(i).isActive() != this.examsOriginal.get(i).isActive()) {
                this.eventDAO.updateEvent(this.exams.get(i));
            }
        }

        this.eventDAO.close();
        setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");

    }

    public void deleteAllButtonClicked(ActionEvent actionEvent) throws IOException, SQLException {
        this.eventDAO.close();
        App.modalStage = new Stage();
        modalWindow = new ModalWindow(App.modalStage, "delete_all_events_window", SubjectsWindowController.examEvent.getNameOfEvent() + " exam dates", "exam_events_window", "", Event.EventType.EXAM.getValue());
        setAndShowModalWindow(modalWindow);
    }
}

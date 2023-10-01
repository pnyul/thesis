package org.thesis.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
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

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.thesis.utility.StageAndSceneInitializer.*;

/**
 * This class is responsible for listing the user's courses and changing their settings.
 */
public class SubjectsWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(SubjectsWindowController.class);

    @FXML
    private Button saveButton;

    @FXML
    private Button addButton;

    @FXML
    private Button backButton;

    @FXML
    private Button learningSettingsButton;

    @FXML
    private TableView<Subject> subjectsTableView;

    @FXML
    private TableColumn<Subject, String> subjectColumn;

    @FXML
    private TableColumn<Subject, Integer> creditColumn;

    @FXML
    private TableColumn<Subject, Integer> examsLeftColumn;

    @FXML
    private TableColumn<Subject, Integer> hoursNeededForLearningColumn;

    @FXML
    private TableColumn<Subject, Boolean> preRequirementColumn;

    @FXML
    private TableColumn<Subject, Boolean> successColumn;

    @FXML
    private TableColumn<Subject, Void> examDatesColumn;

    @FXML
    private TableColumn<Subject, Boolean> furtherRegPossibleColumn;

    @FXML
    private TableColumn<Subject, Boolean> suppExamColumn;

    @FXML
    private TableColumn<Subject, Void> deleteColumn;

    private SubjectDAO subjectDAO;
    private EventDAO eventDAO;
    private List<Subject> subjects;
    private List<Subject> subjectsOriginal;
    private List<Event> exams;
    public static Event examEvent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

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
            this.exams = this.eventDAO.findAllEvents(App.user.getUsername(), Event.EventType.EXAM.getValue());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        examEvent = new Event();

        saveButton.setGraphic(new ImageView("/icons/save.png"));
        Tooltip.install(this.saveButton, new Tooltip("Save"));
        addButton.setGraphic(new ImageView("/icons/plus.png"));
        Tooltip.install(this.addButton, new Tooltip("Add new subject"));
        backButton.setGraphic(new ImageView("/icons/back.png"));
        Tooltip.install(this.backButton, new Tooltip("Back"));
        learningSettingsButton.setGraphic(new ImageView("/icons/support.png"));
        Tooltip.install(this.learningSettingsButton, new Tooltip("Learning habits"));

        this.subjectsOriginal = new ArrayList<>();

        for (int i = 0; i < this.subjects.size(); i++) {

            Subject subject = new Subject();

            subject.setId(this.subjects.get(i).getId());
            subject.setSubject(this.subjects.get(i).getSubject());
            subject.setUser(this.subjects.get(i).getUser());
            subject.setCredit(this.subjects.get(i).getCredit());
            subject.setExamsLeft(this.subjects.get(i).getExamsLeft());
            subject.setFurtherRegistrationPossible(this.subjects.get(i).isFurtherRegistrationPossible());
            subject.setTimeNeededForLearning(this.subjects.get(i).getTimeNeededForLearning());
            subject.setPreRequirement(this.subjects.get(i).isPreRequirement());
            subject.setSuccess(this.subjects.get(i).isSuccess());
            subject.setSupplementaryExamAllowed(this.subjects.get(i).getSupplementaryExamAllowed());

            this.subjectsOriginal.add(subject);
        }


        this.subjectsTableView.getItems().setAll(this.subjects);

        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        creditColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        examsLeftColumn.setCellValueFactory(new PropertyValueFactory<>("examsLeft"));
        hoursNeededForLearningColumn.setCellValueFactory(new PropertyValueFactory<>("timeNeededForLearning"));

        this.creditColumn.setCellFactory(subjectIntegerTableColumn -> new TextFieldTableCell<>());
        this.creditColumn.setCellFactory(ComboBoxTableCell.forTableColumn(initializeComboBox(0, 20)));
        this.examsLeftColumn.setCellFactory(subjectIntegerTableColumn -> new TextFieldTableCell<>());
        this.examsLeftColumn.setCellFactory(ComboBoxTableCell.forTableColumn(initializeComboBox(1, App.user.getMaxPossibleExam())));
        this.hoursNeededForLearningColumn.setCellFactory(subjectIntegerTableColumn -> new TextFieldTableCell<>());
        this.hoursNeededForLearningColumn.setCellFactory(ComboBoxTableCell.forTableColumn(initializeComboBox(0, 50)));

        Label examsLeftLabel = new Label("Exam(s) left(sum)");
        examsLeftLabel.setTooltip(new Tooltip("The total number of exams remaining."));
        this.examsLeftColumn.setGraphic(examsLeftLabel);

        Label furtherRegLabel = new Label("Reg?");
        furtherRegLabel.setTooltip(new Tooltip("Is further registration possible?"));
        this.furtherRegPossibleColumn.setGraphic(furtherRegLabel);

        Label learningTimeLabel = new Label("Learning time(hours)");
        learningTimeLabel.setTooltip(new Tooltip("The daily learning time in hours."));
        this.hoursNeededForLearningColumn.setGraphic(learningTimeLabel);

        Label preRequirementLabel = new Label("Pre-Requirement");
        preRequirementLabel.setTooltip(new Tooltip("Is there a pre-requirement for the subject?"));
        this.preRequirementColumn.setGraphic(preRequirementLabel);

        Label successLabel = new Label("Success");
        successLabel.setTooltip(new Tooltip("Is the subject successful?"));
        this.successColumn.setGraphic(successLabel);

        Label suppExamLabel = new Label("Supp. exam poss?");
        suppExamLabel.setTooltip(new Tooltip("Is supplementary exam possible?"));
        this.suppExamColumn.setGraphic(suppExamLabel);

        this.subjectColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        this.creditColumn.setStyle("-fx-alignment: CENTER;");
        this.hoursNeededForLearningColumn.setStyle("-fx-alignment: CENTER;");
        this.examsLeftColumn.setStyle("-fx-alignment: CENTER;");

        Callback<TableColumn<Subject, Void>, TableCell<Subject, Void>> examDatesCellFactory = new Callback<>() {
            @Override
            public TableCell<Subject, Void> call(final TableColumn<Subject, Void> param) {
                final TableCell<Subject, Void> cell = new TableCell<>() {

                    private final Button button = new Button("Show");

                    {
                        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                        button.setOnAction((ActionEvent event) -> {
                            Subject data = getTableView().getItems().get(getIndex());
                            examEvent.setNameOfEvent(data.getSubject());

                            for (int i = 0; i < subjectsOriginal.size(); i++) {
                                if (subjectsOriginal.get(i).getCredit() != subjects.get(i).getCredit() || subjectsOriginal.get(i).getExamsLeft() != subjects.get(i).getExamsLeft() || subjectsOriginal.get(i).getTimeNeededForLearning() != subjects.get(i).getTimeNeededForLearning() || subjectsOriginal.get(i).isPreRequirement() != subjects.get(i).isPreRequirement() || subjectsOriginal.get(i).isSuccess() != subjects.get(i).isSuccess() || subjectsOriginal.get(i).isFurtherRegistrationPossible() != subjects.get(i).isFurtherRegistrationPossible() || subjectsOriginal.get(i).getSupplementaryExamAllowed() != subjects.get(i).getSupplementaryExamAllowed()) {
                                    try {
                                        subjectDAO.updateSubject(subjects.get(i));
                                    } catch (SQLException e) {
                                        logger.log(Level.ERROR, e);
                                    }
                                }

                                if (subjectsOriginal.get(i).isSuccess() != subjects.get(i).isSuccess()) {
                                    if (subjects.get(i).isSuccess()) {
                                        for (Event exam : exams) {
                                            if (exam.getUsername().equals(App.user.getUsername()) && exam.getNameOfEvent().equals(subjects.get(i).getSubject())) {
                                                Event newEvent = exam;
                                                newEvent.setActive(false);
                                                try {
                                                    eventDAO.updateEvent(newEvent);
                                                } catch (SQLException e) {
                                                    logger.log(Level.ERROR, e);
                                                }
                                            }
                                        }
                                    } else {
                                        for (Event exam : exams) {
                                            if (exam.getUsername().equals(App.user.getUsername()) && exam.getNameOfEvent().equals(subjects.get(i).getSubject())) {
                                                Event newEvent = exam;
                                                newEvent.setActive(true);
                                                try {
                                                    eventDAO.updateEvent(newEvent);
                                                } catch (SQLException e) {
                                                    logger.log(Level.ERROR, e);
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            try {
                                eventDAO.close();
                            } catch (SQLException e) {
                                logger.log(Level.ERROR, e);
                            }

                            try {
                                subjectDAO.close();
                            } catch (SQLException e) {
                                logger.log(Level.ERROR, e);
                            }
                            setAndShowStageAndScene(App.stage, data.getSubject() + " exam dates", "exam_events_window");
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

        this.examDatesColumn.setCellFactory(examDatesCellFactory);

        Callback<TableColumn<Subject, Void>, TableCell<Subject, Void>> deleteCellFactory = new Callback<>() {
            @Override
            public TableCell<Subject, Void> call(final TableColumn<Subject, Void> param) {
                final TableCell<Subject, Void> cell = new TableCell<>() {

                    private final Button button = new Button();


                    {
                        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        button.setGraphic(new ImageView("/icons/delete_trash.png"));
                        Tooltip.install(this.button, new Tooltip("Delete subject"));
                        button.setOnAction((ActionEvent event) -> {
                            Subject data = getTableView().getItems().get(getIndex());
                            try {
                                subjectDAO.deleteSubject(data);
                            } catch (SQLException e) {
                                logger.log(Level.ERROR, e);
                            }

                            for (Event exam : exams) {
                                if (exam.getUsername().equals(App.user.getUsername()) && exam.getNameOfEvent().equals(data.getSubject())) {
                                    try {
                                        eventDAO.deleteEvent(exam);
                                    } catch (SQLException e) {
                                        logger.log(Level.ERROR, e);
                                    }
                                }
                            }

                            setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");

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

        this.furtherRegPossibleColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Subject, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Subject, Boolean> value) {
                return value.getValue().furtherRegistrationPossibleProperty();
            }
        });

        furtherRegPossibleColumn.setCellFactory(CheckBoxTableCell.forTableColumn(furtherRegPossibleColumn));


        this.preRequirementColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Subject, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Subject, Boolean> value) {
                return value.getValue().preRequirementProperty();
            }
        });

        preRequirementColumn.setCellFactory(CheckBoxTableCell.forTableColumn(preRequirementColumn));


        this.successColumn.setCellValueFactory(value -> value.getValue().successProperty());
        this.successColumn.setCellFactory(CheckBoxTableCell.forTableColumn(successColumn));


        this.suppExamColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Subject, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Subject, Boolean> value) {
                return value.getValue().supplementaryExamAllowedProperty();
            }
        });

        suppExamColumn.setCellFactory(CheckBoxTableCell.forTableColumn(suppExamColumn));

        this.subjectsTableView.setEditable(true);

        subjectsTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    public void saveButtonClicked(ActionEvent actionEvent) throws SQLException {

        for (int i = 0; i < this.subjectsOriginal.size(); i++) {
            if (this.subjectsOriginal.get(i).getCredit() != this.subjects.get(i).getCredit() || this.subjectsOriginal.get(i).getExamsLeft() != this.subjects.get(i).getExamsLeft() || this.subjectsOriginal.get(i).getTimeNeededForLearning() != this.subjects.get(i).getTimeNeededForLearning() || this.subjectsOriginal.get(i).isPreRequirement() != this.subjects.get(i).isPreRequirement() || this.subjectsOriginal.get(i).isSuccess() != this.subjects.get(i).isSuccess() || this.subjectsOriginal.get(i).isFurtherRegistrationPossible() != this.subjects.get(i).isFurtherRegistrationPossible() || this.subjectsOriginal.get(i).getSupplementaryExamAllowed() != this.subjects.get(i).getSupplementaryExamAllowed()) {
                this.subjectDAO.updateSubject(this.subjects.get(i));
            }

            if (this.subjectsOriginal.get(i).isSuccess() != this.subjects.get(i).isSuccess()) {
                if (this.subjects.get(i).isSuccess()) {
                    for (Event exam : this.exams) {
                        if (exam.getUsername().equals(App.user.getUsername()) && exam.getNameOfEvent().equals(this.subjects.get(i).getSubject())) {
                            Event event = exam;
                            event.setActive(false);
                            this.eventDAO.updateEvent(event);
                        }
                    }
                } else {
                    for (Event exam : exams) {
                        if (exam.getUsername().equals(App.user.getUsername()) && exam.getNameOfEvent().equals(this.subjects.get(i).getSubject())) {
                            Event event = exam;
                            event.setActive(true);
                            this.eventDAO.updateEvent(event);
                        }
                    }
                }
            }
        }

        this.subjectDAO.close();
        this.eventDAO.close();

        setAndShowStageAndScene(App.stage, "Main menu", "main_menu_window");

    }


    public ObservableList<Integer> initializeComboBox(int min, int max) {
        ObservableList<Integer> list = FXCollections.observableArrayList();

        for (int i = min; i <= max; i++) {
            list.add(Integer.valueOf(i));
        }

        return list;
    }

    public void creditEditCommit(TableColumn.CellEditEvent<Subject, Integer> creditCell) {
        this.subjects.get(findSubject(this.subjectsTableView.getSelectionModel().getSelectedItem().getSubject())).setCredit(creditCell.getNewValue());
    }

    public void examsLeftEditCommit(TableColumn.CellEditEvent<Subject, Integer> examsLeftCell) {
        this.subjects.get(findSubject(this.subjectsTableView.getSelectionModel().getSelectedItem().getSubject())).setExamsLeft(examsLeftCell.getNewValue());
    }

    public void isFurtherRegPossible(TableColumn.CellEditEvent<Subject, Boolean> furtherRegCell) {
        this.subjects.get(findSubject(this.subjectsTableView.getSelectionModel().getSelectedItem().getSubject())).setPreRequirement(furtherRegCell.getNewValue());

    }

    public void learningTimeEditCommit(TableColumn.CellEditEvent<Subject, Integer> learningTimeCell) {
        this.subjects.get(findSubject(this.subjectsTableView.getSelectionModel().getSelectedItem().getSubject())).setTimeNeededForLearning(learningTimeCell.getNewValue());

    }

    public void preRequirementEditCommit(TableColumn.CellEditEvent<Subject, Boolean> preRequirementCell) {
        this.subjects.get(findSubject(this.subjectsTableView.getSelectionModel().getSelectedItem().getSubject())).setPreRequirement(preRequirementCell.getNewValue());

    }

    public void successEditCommit(TableColumn.CellEditEvent<Subject, Boolean> successCell) {
        this.subjects.get(findSubject(this.subjectsTableView.getSelectionModel().getSelectedItem().getSubject())).setSuccess(successCell.getNewValue());

    }

    public void suppExamColumnEditCommit(TableColumn.CellEditEvent<Subject, Boolean> suppExamAllowedCell) {
        this.subjects.get(findSubject(this.subjectsTableView.getSelectionModel().getSelectedItem().getSubject())).setSupplementaryExamAllowed(suppExamAllowedCell.getNewValue());
    }


    public int findSubject(String modifiableSubject) {

        for (int i = 0; i < this.subjects.size(); i++) {
            if (this.subjects.get(i).getSubject().equals(modifiableSubject)) {
                return i;
            }
        }

        return -1;

    }


    public void learningHabitsButtonClicked(ActionEvent actionEvent) throws SQLException {
        closeConnection();
        setAndShowStageAndScene(App.stage, "Learning habits", "learning_habits_window");
    }

    public void addButtonClicked(ActionEvent actionEvent) throws SQLException {
        closeConnection();
        setAndShowStageAndScene(App.stage, "Add subject", "add_subject_window");
    }

    public void backButtonClicked(ActionEvent actionEvent) throws SQLException {
        closeConnection();
        setAndShowStageAndScene(App.stage, "Main window", "main_menu_window");
    }


    private void closeConnection() throws SQLException {
        this.subjectDAO.close();
        this.eventDAO.close();
    }


}

package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
import org.thesis.model.SortingAlgorithm;
import org.thesis.model.Subject;
import org.thesis.utility.ModalWindow;

import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.*;

import static org.thesis.utility.StageAndSceneInitializer.*;
import static org.thesis.utility.StageAndSceneInitializer.setAndShowModalWindow;

/**
 * This class represents the calendar and uses its methods to populate it with events.
 */
public class CalendarWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(CalendarWindowController.class);
    public static final int NUMBER_OF_CELLS = 49;
    public static final int STARTING_CELL = 7;
    public static final int NEXT_MONTH_START = 34;

    @FXML
    private Button prevButton;

    @FXML
    private Button nextButton;

    @FXML
    private Label yearLabel;

    @FXML
    private Label monthLabel;

    @FXML
    private GridPane calendarGridPane;

    private SubjectDAO subjectDAO;
    private List<Subject> subjectList;
    private EventDAO eventDAO;
    private List<Event> eventList;
    private SortingAlgorithm sortingAlgorithm;
    private List<Event> eventsForTheCalendar;
    private LocalDate current = App.user.getBeginOfExamPeriod().withDayOfMonth(1);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.subjectDAO = new SubjectDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.subjectList = this.subjectDAO.findAllSubjects(App.user.getUsername());
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
            this.eventList = this.eventDAO.findAllEvents(App.user.getUsername());
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        try {
            this.eventDAO.close();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        this.calendarGridPane.setGridLinesVisible(true);

        this.prevButton.setGraphic(new ImageView("/icons/left.png"));
        this.nextButton.setGraphic(new ImageView("/icons/right.png"));

        this.sortingAlgorithm = new SortingAlgorithm(App.user, this.subjectList, this.eventList);
        this.eventsForTheCalendar = this.sortingAlgorithm.proposedExamDatesWithGeneralEvents();

        calculateDate();

    }

    private void calculateDate() {

        this.yearLabel.setText(String.valueOf(this.current.getYear()));
        this.monthLabel.setText(String.valueOf(this.current.getMonth()));
        this.current = this.current.minusDays((long) this.current.getDayOfWeek().getValue() - 1);

        ListView<Text> listView;
        Label label;
        String date = null;
        String beginHour;
        String beginMinute;
        String endHour;
        String endMinute;

        for (int i = STARTING_CELL; i < NUMBER_OF_CELLS; i++) {

            Text text;

            listView = (ListView<Text>) ((VBox) ((HBox) this.calendarGridPane.getChildren().get(i)).getChildren().get(0)).getChildren().get(0);


            listView.setEditable(false);
            label = ((Label) ((VBox) ((HBox) this.calendarGridPane.getChildren().get(i)).getChildren().get(1)).getChildren().get(0));

            for (Event event : this.eventsForTheCalendar) {
                if ((event.getBeginOfEvent().isEqual(this.current.atStartOfDay()) || event.getBeginOfEvent().isAfter(this.current.atStartOfDay())) && event.getEndOfEvent().isBefore(this.current.plusDays(1).atStartOfDay())) {
                    beginHour = event.getBeginOfEvent().getHour() < 10 ? "0" + event.getBeginOfEvent().getHour() : String.valueOf(event.getBeginOfEvent().getHour());
                    beginMinute = event.getBeginOfEvent().getMinute() < 10 ? "0" + event.getBeginOfEvent().getMinute() : String.valueOf(event.getBeginOfEvent().getMinute());
                    endHour = event.getEndOfEvent().getHour() < 10 ? "0" + event.getEndOfEvent().getHour() : String.valueOf(event.getEndOfEvent().getHour());
                    endMinute = event.getEndOfEvent().getMinute() < 10 ? "0" + event.getEndOfEvent().getMinute() : String.valueOf(event.getEndOfEvent().getMinute());
                    date = event.getBeginOfEvent().toLocalDate().toString();
                    text = new Text(beginHour + ":" + beginMinute + "-" + endHour + ":" + endMinute + " " + event.getNameOfEvent());

                    if (event.getType() == Event.EventType.EXAM.getValue()) {
                        text.setFill(Color.RED);
                    }

                    listView.getItems().add(text);

                }
            }


            if (listView.getItems().isEmpty()) {
                listView.setStyle("-fx-background-color: #cccaca");
            }


            ListView<Text> finalListView = listView;

            String finalDate = date;

            listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    if (!finalListView.getItems().isEmpty()) {

                        list = new ArrayList<>();
                        list.add(new Text(finalDate));

                        for (int i = 0; i < finalListView.getItems().size(); i++) {
                            list.add(new Text(finalListView.getItems().get(i).getText()));
                        }
                        App.modalStage = new Stage();
                        modalWindow = new ModalWindow(App.modalStage, "daily_view_window", "", "", "");
                        setAndShowModalWindow(modalWindow);
                    }
                }
            });


            label.setText(String.valueOf(this.current.getDayOfMonth()));
            label.setStyle("-fx-border-color: #000000");

            if (i > NEXT_MONTH_START) {
                if (this.current.getDayOfMonth() == 1) {
                    label.setStyle("-fx-border-color: #0000ff; -fx-text-fill: #0000ff");
                }
            }

            this.current = this.current.plusDays(1);
        }

    }


    public void prevButtonClicked(ActionEvent actionEvent) {
        this.current = this.current.minusMonths(2).withDayOfMonth(1);
        setCalendar();
    }

    public void nextButtonClicked(ActionEvent actionEvent) {
        this.current = LocalDate.of(this.current.getYear(), this.current.getMonth(), 1);
        setCalendar();
    }

    private void setDate() {
        this.yearLabel.setText(String.valueOf(this.current.getYear()));
        this.monthLabel.setText(String.valueOf(this.current.getMonth()));
    }

    public void backPressed(ActionEvent actionEvent) {
        setAndShowStageAndScene(App.stage, "Main menu", "main_menu_window");
    }

    private void clearAll() {
        for (int i = STARTING_CELL; i < NUMBER_OF_CELLS; i++) {
            ((ListView<String>) ((VBox) ((HBox) this.calendarGridPane.getChildren().get(i)).getChildren().get(0)).getChildren().get(0)).getItems().clear();
            ((Label) ((VBox) ((HBox) this.calendarGridPane.getChildren().get(i)).getChildren().get(1)).getChildren().get(0)).setStyle(null);
        }
    }

    private void setCalendar() {
        clearAll();
        setDate();
        calculateDate();
    }

}

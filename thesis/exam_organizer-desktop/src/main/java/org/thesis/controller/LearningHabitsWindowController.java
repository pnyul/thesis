package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.UserDAO;
import org.thesis.dao.UserDAOImpl;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.thesis.utility.StageAndSceneInitializer.*;
import static org.thesis.utility.ComboBoxInitializer.*;

/**
 * This class manages the learning habits of the user.
 */
public class LearningHabitsWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(LearningHabitsWindowController.class);

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @FXML
    private CheckBox saturdayCheckBox;

    @FXML
    private CheckBox sundayCheckBox;

    @FXML
    private ComboBox<Integer> learningTimePerDayComboBox;

    private UserDAO userDAO;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            this.userDAO = new UserDAOImpl();
        } catch (SQLException e) {
            logger.log(Level.ERROR, e);
        }

        saveButton.setGraphic(new ImageView("/icons/save.png"));
        Tooltip.install(this.saveButton, new Tooltip("Save"));
        backButton.setGraphic(new ImageView("/icons/back.png"));
        Tooltip.install(this.backButton, new Tooltip("Back"));

        this.saturdayCheckBox.setSelected(App.user.isRestDayOnSaturday());
        this.sundayCheckBox.setSelected(App.user.isRestDayOnSunday());

        setComboBox(learningTimePerDayComboBox, 1, 12, App.user.getLearningTimePerDay() - 1, Type.INTEGER.getValue());

    }


    public void saveButtonClicked(ActionEvent actionEvent) throws SQLException {

        boolean isChanged = false;

        if (App.user.getLearningTimePerDay() != this.learningTimePerDayComboBox.getValue()) {
            App.user.setLearningTimePerDay(this.learningTimePerDayComboBox.getValue());
            isChanged = true;
        }

        if (App.user.isRestDayOnSaturday() != this.saturdayCheckBox.isSelected()) {
            App.user.setRestDayOnSaturday(this.saturdayCheckBox.isSelected());
            isChanged = true;
        }

        if (App.user.isRestDayOnSunday() != this.sundayCheckBox.isSelected()) {
            App.user.setRestDayOnSunday(this.sundayCheckBox.isSelected());
            isChanged = true;
        }

        if (isChanged) {
            this.userDAO.updateUserLearningHabits(App.user);
        }

        this.userDAO.close();
        setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");

    }

    public void backButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.userDAO.close();
        setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");
    }

}

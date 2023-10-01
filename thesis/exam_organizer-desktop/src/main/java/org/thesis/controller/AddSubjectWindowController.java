package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;
import org.thesis.dao.SubjectDAO;
import org.thesis.dao.SubjectDAOImpl;
import org.thesis.model.Subject;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static org.thesis.utility.StageAndSceneInitializer.*;
import static org.thesis.utility.ComboBoxInitializer.*;

/**
 * Methods of this class adds new subject after checking the given inputs.
 */
public class AddSubjectWindowController implements Initializable {

    private static final Logger logger = LogManager.getLogger(AddSubjectWindowController.class);

    @FXML
    private Button saveButton;

    @FXML
    private Button backButton;

    @FXML
    private CheckBox furtherRegCheckBox;

    @FXML
    private TextField subjectTextField;

    @FXML
    private ComboBox<Integer> creditComboBox;

    @FXML
    private ComboBox<Integer> examsLeftComboBox;

    @FXML
    private ComboBox<Integer> hoursNeededComboBox;

    @FXML
    private CheckBox prerequirementCheckBox;

    private SubjectDAO subjectDAO;
    private List<Subject> subjects;

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

        saveButton.setGraphic(new ImageView("/icons/save.png"));
        Tooltip.install(this.saveButton, new Tooltip("Add subject"));
        backButton.setGraphic(new ImageView("/icons/back.png"));
        Tooltip.install(this.backButton, new Tooltip("Back"));

        setComboBox(this.creditComboBox, 0, 20, 0, Type.INTEGER.getValue());
        setComboBox(this.examsLeftComboBox, 1, App.user.getMaxPossibleExam(), 0, Type.INTEGER.getValue());
        setComboBox(this.hoursNeededComboBox, 1, 50, 4, Type.INTEGER.getValue());

    }

    public void saveButtonClicked(ActionEvent actionEvent) throws SQLException {

        if (Objects.equals(this.subjectTextField.getText(), "")) {
            this.subjectTextField.setStyle("-fx-text-box-border: #ff0000;");
            this.subjectTextField.setPromptText("Empty subject name.");
            return;
        }

        if (isThisSubjectUnique()) {
            this.subjectTextField.setStyle("-fx-text-box-border: #ff0000;");
            this.subjectTextField.setText("");
            this.subjectTextField.setPromptText("This subject already exists.");
            return;
        }

        Subject subject = new Subject();

        subject.setSubject(this.subjectTextField.getText());
        subject.setUser(App.user.getUsername());
        subject.setCredit(this.creditComboBox.getValue());
        subject.setExamsLeft(this.examsLeftComboBox.getValue());
        subject.setFurtherRegistrationPossible(this.furtherRegCheckBox.isSelected());
        subject.setTimeNeededForLearning(this.hoursNeededComboBox.getValue());
        subject.setPreRequirement(this.prerequirementCheckBox.isSelected());
        subject.setSuccess(false);

        this.subjectDAO.insertSubject(subject);

        this.subjectDAO.close();


        setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");

    }

    private boolean isThisSubjectUnique() {
        for (Subject subject : this.subjects) {
            if (subject.getSubject().equals(this.subjectTextField.getText())) {
                return true;
            }
        }
        return false;
    }

    public void backButtonClicked(ActionEvent actionEvent) throws SQLException {
        this.subjectDAO.close();
        setAndShowStageAndScene(App.stage, "Subjects", "subjects_window");
    }

    public void subjectTextFieldClicked(MouseEvent actionEvent) {
        this.subjectTextField.setStyle("-fx-text-box-border: #000000;");
        this.subjectTextField.setText("");
    }
}

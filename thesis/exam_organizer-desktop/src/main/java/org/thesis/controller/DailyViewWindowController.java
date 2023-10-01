package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.thesis.utility.StageAndSceneInitializer;

import java.net.URL;
import java.util.ResourceBundle;

import static org.thesis.App.modalStage;

/**
 * This class is responsible for the daily view of the calendar.
 */
public class DailyViewWindowController implements Initializable {

    @FXML
    private Button okButton;

    @FXML
    private Label dateLabel;

    @FXML
    public ListView<Text> dailyListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.okButton.setGraphic(new ImageView("/icons/done.png"));

        dateLabel.setText(StageAndSceneInitializer.list.get(0).getText());

        for (int i = 1; i < StageAndSceneInitializer.list.size(); i++) {
            dailyListView.getItems().add(StageAndSceneInitializer.list.get(i));
        }

    }


    public void backButtonClicked(ActionEvent actionEvent) {
        modalStage.close();
    }


}

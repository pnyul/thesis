package org.thesis.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.thesis.App;

import java.net.URL;
import java.util.ResourceBundle;

import static org.thesis.utility.StageAndSceneInitializer.modalWindow;
import static org.thesis.utility.StageAndSceneInitializer.setAndShowStageAndScene;

/**
 * This class controls the modal window.
 */
public class MessageWindowController implements Initializable {

    @FXML
    private Button okButton;

    @FXML
    private Label messageLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.messageLabel.setText(modalWindow.getNewMessage());
        okButton.setGraphic(new ImageView("/icons/done.png"));
    }

    public void okButtonClicked(ActionEvent actionEvent) {
        App.modalStage.close();
        if (!modalWindow.getNewTitle().isEmpty() && !modalWindow.getNewFxml().isEmpty()) {
            setAndShowStageAndScene(App.stage, modalWindow.getNewTitle(), modalWindow.getNewFxml());
        }
    }

}

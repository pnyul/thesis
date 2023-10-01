package org.thesis.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.thesis.App;

import java.io.IOException;
import java.util.List;

/**
 * This class is responsible for setting the application windows.
 */
public class StageAndSceneInitializer {

    private static final Logger logger = LogManager.getLogger(StageAndSceneInitializer.class);
    public static final String TEXT_BOX_BORDER_COLOR_RED = "-fx-text-box-border: #ff0000;";
    public static final String TEXT_BOX_BORDER_COLOR_GREEN = "-fx-text-box-border: #00ff00;";
    public static final String TEXT_BOX_BORDER_COLOR_BLACK = "-fx-text-box-border: #000000;";
    public static final String MID_TEXT_COLOR_RED = "-fx-mid-text-color: #ff0000;";
    public static final String MID_TEXT_COLOR_GREEN = "-fx-mid-text-color: #00ff00;";

    public static ModalWindow modalWindow;

    public static List<Text> list;

    private StageAndSceneInitializer() {
    }

    public static void setAndShowModalWindow(ModalWindow modalWindow) {
        loadFXML("/fxml/" + modalWindow.getModalFxml() + ".fxml", modalWindow.getModalStage());
        modalWindow.getModalStage().setResizable(false);
        modalWindow.getModalStage().initModality(Modality.APPLICATION_MODAL);
        modalWindow.getModalStage().getIcons().add(new Image("/icons/important.png"));
        modalWindow.getModalStage().setOnCloseRequest(windowEvent -> windowEvent.consume());
        modalWindow.getModalStage().showAndWait();
    }

    public static void setAndShowStageAndScene(Stage stage, String title, String fxmlName) {
        loadFXML("/fxml/" + fxmlName + ".fxml", stage);
        stage.setTitle(title);
        stage.show();
    }

    public static FXMLLoader loadFXML(String fxml, Stage stage) {

        FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));

        Scene scene = null;

        try {
            Parent root = loader.load();
            scene = new Scene(root);
        } catch (IOException e) {
            logger.log(Level.ERROR, e);
        }

        stage.setScene(scene);

        scene.getStylesheets().add(App.class.getResource("/css/style.css").toExternalForm());
        scene.getWindow().centerOnScreen();

        return loader;

    }

}

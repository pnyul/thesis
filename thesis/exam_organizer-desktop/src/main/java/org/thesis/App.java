package org.thesis;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.thesis.model.User;

import static org.thesis.utility.StageAndSceneInitializer.setAndShowStageAndScene;

/**
 * The main method in this class is the entry point of the application.
 */

public class App extends javafx.application.Application {

    public static Stage stage;
    public static Stage modalStage;
    public static User user;

    @Override
    public void start(Stage stage) {

        this.user = new User();
        App.stage = stage;

        App.stage.setResizable(false);
        App.stage.getIcons().add(new Image("/icons/calendar.png"));

        setAndShowStageAndScene(App.stage,"Login", "login_window");

    }

    public static void main(String[] args) {
        launch();
    }

}
package game.gui;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DooR DasH: Scare vs Laugh Touchdown");
        GameController controller = new GameController(primaryStage);
        controller.showStartScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

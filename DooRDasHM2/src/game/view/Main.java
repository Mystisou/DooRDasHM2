package game.view;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        ViewManager.setStage(primaryStage);
        
        StartView startScreen = new StartView();
        
        ViewManager.updateView(startScreen);
        
        primaryStage.setTitle("DooR DasH");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args); // Launches JavaFX 
    }
}
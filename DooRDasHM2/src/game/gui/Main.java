package game.gui;

import game.gui.controller.StartController;
import game.gui.view.StartView;
import game.gui.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        ResourceLoader.preload();
        ViewManager.setStage(stage);
        stage.setTitle("DoorDasH");
        stage.show();

        StartView sv = new StartView();
        new StartController(sv);          // controller wires all button logic
        ViewManager.updateView(sv);
    }

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }
}

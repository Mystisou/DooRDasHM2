package game.gui;

import game.gui.controller.StartController;
import game.gui.view.StartView;
import game.gui.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    static {
       
        Logger cssLog = Logger.getLogger("javafx.scene.CssStyleHelper");
        cssLog.setLevel(Level.SEVERE);
        cssLog.setUseParentHandlers(false);
    }

   
    public void start(Stage stage) {
        ResourceLoader.preload();
        ViewManager.setStage(stage);
        stage.setTitle("DoorDasH");
        stage.show();

        StartView sv = new StartView();
        new StartController(sv);
        ViewManager.updateView(sv);
    }

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        launch(args);
    }
}

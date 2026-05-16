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
        // Silence JavaFX 8 Modena CSS lookup warnings.
        // setUseParentHandlers(false) is the key — without it the WARNING
        // propagates to the root logger's handlers even though the named
        // logger's own level is set to SEVERE.
        Logger cssLog = Logger.getLogger("javafx.scene.CssStyleHelper");
        cssLog.setLevel(Level.SEVERE);
        cssLog.setUseParentHandlers(false);
    }

    @Override
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

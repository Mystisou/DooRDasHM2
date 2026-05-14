package game.gui;

import game.engine.Game;
import game.engine.Role;
import game.gui.controller.GameController;
import game.gui.view.GameView;
import game.gui.view.ViewManager;

public final class GameLauncher {

    public static void launch(String role) throws Exception {
        Game game = new Game(Role.valueOf(role));
        GameView gameView = new GameView();
        new GameController(game, gameView);
        ViewManager.updateView(gameView);
    }

    private GameLauncher() {}
}

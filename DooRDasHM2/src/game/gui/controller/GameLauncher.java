package game.gui.controller;

import game.engine.Game;
import game.engine.Role;
import game.gui.view.GameView;
import game.gui.view.ViewManager;

/**
 * Controller helper that bootstraps a new game:
 * creates the Game (model), the GameView (view), wires them via GameController,
 * then hands the view to ViewManager.
 */
public final class GameLauncher {

    public static void launch(String role) throws Exception {
        Game     game     = new Game(Role.valueOf(role));
        GameView gameView = new GameView();
        new GameController(game, gameView);
        ViewManager.updateView(gameView);
    }

    private GameLauncher() {}
}

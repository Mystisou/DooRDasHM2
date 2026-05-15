package game.gui.controller;

import game.gui.view.InstructionsView;

/**
 * Handles the "ENTER THE FLOOR" button on the instructions screen.
 * Launches the game via GameLauncher when the player is ready.
 */
public class InstructionsController {

    private final InstructionsView view;
    private final String           selectedRole;

    public InstructionsController(InstructionsView view, String selectedRole) {
        this.view         = view;
        this.selectedRole = selectedRole;
        wireHandlers();
    }

    private void wireHandlers() {
        view.getStartButton().setOnAction(e -> {
            try {
                GameLauncher.launch(selectedRole);
            } catch (Exception ex) {
                // engine errors (bad CSV etc.) are rare; surface them without crashing
                view.getStartButton().setText("Error — check console");
                ex.printStackTrace();
            }
        });
    }
}

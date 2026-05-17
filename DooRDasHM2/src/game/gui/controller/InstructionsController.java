package game.gui.controller;

import game.gui.view.InstructionsView;

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
                
                view.getStartButton().setText("Error — check console");
                ex.printStackTrace();
            }
        });
    }
}

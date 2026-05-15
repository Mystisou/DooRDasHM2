package game.gui.controller;

import game.gui.view.InstructionsView;
import game.gui.view.StartView;
import game.gui.view.ViewManager;

/**
 * Handles all interaction on the start screen:
 * role-card selection and the START button action.
 * StartView knows nothing about game state or navigation.
 */
public class StartController {

    private String selectedRole = null;
    private final StartView view;

    public StartController(StartView view) {
        this.view = view;
        wireHandlers();
    }

    private void wireHandlers() {
        view.getScarerCard().setOnMouseClicked(e -> {
            selectedRole = "SCARER";
            view.selectScarer();
        });

        view.getLaugherCard().setOnMouseClicked(e -> {
            selectedRole = "LAUGHER";
            view.selectLaugher();
        });

        view.getStartButton().setOnAction(e -> handleStart());
    }

    private void handleStart() {
        if (selectedRole == null) {
            view.showNoRolePopup();
            return;
        }
        try {
            InstructionsView iv = new InstructionsView(selectedRole);
            new InstructionsController(iv, selectedRole);
            ViewManager.updateView(iv);
        } catch (Exception ex) {
            view.showError("Failed to load: " + ex.getMessage());
        }
    }
}

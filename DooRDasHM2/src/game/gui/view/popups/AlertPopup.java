package game.gui.view.popups;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public final class AlertPopup {

    public static void show(String title, String message, Window owner) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) alert.initOwner(owner);
        alert.showAndWait();
    }

    private AlertPopup() {}
}

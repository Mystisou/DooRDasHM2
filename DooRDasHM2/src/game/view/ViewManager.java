package game.view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ViewManager {
	private static Stage ViewStage;

	public static void setStage(Stage s) {
		ViewStage = s;
	}

	public static void updateView(Pane newLayout) {
		if (ViewStage.getScene() == null) 
			ViewStage.setScene(new Scene(newLayout, 1000, 800)); 
		else 
			ViewStage.getScene().setRoot(newLayout);

	}
}



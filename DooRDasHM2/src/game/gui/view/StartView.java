package game.gui.view;

import game.gui.ResourceLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StartView extends VBox {

    private static final String BG_DARK      = "#0d0d1a";
    private static final String BG_MID       = "#1a1a2e";
    private static final String PURPLE_LIGHT = "#9b59b6";
    private static final String GOLD         = "#f1c40f";
    private static final String SCARER_BLUE  = "#2980b9";
    private static final String LAUGHER_GRN  = "#27ae60";
    private static final String TEXT_MAIN    = "#ecf0f1";
    private static final String TEXT_DIM     = "#95a5a6";
    private static final String RED_BTN      = "#c0392b";
    private static final String RED_HOVER    = "#e74c3c";

    private static final String F_BANGERS = "resources/fonts/Bangers-Regular.ttf";
    private static final String F_PIXEL   = "resources/fonts/PressStart2P-Regular.ttf";
    private static final String F_INTER   = "resources/fonts/Inter-VariableFont_opsz,wght.ttf";

    private String selectedRole = null;

    public StartView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(14);
        this.setPadding(new Insets(28, 60, 28, 60));
        this.setStyle("-fx-background-color: " + BG_DARK + ";");

        Label title = new Label("DooR DasH");
        title.setFont(font(F_BANGERS, 64));
        title.setStyle(
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-effect: dropshadow(gaussian,#6c3483,16,0.6,0,0);"
        );

        Label titleSub = new Label("Scare  vs  Laugh  Touchdown");
        titleSub.setFont(font(F_PIXEL, 10));
        titleSub.setStyle("-fx-text-fill: " + PURPLE_LIGHT + ";");

        VBox howToPlay = buildInstructionsBox();

        Label chooseLabel = new Label(" CHOOSE YOUR SIDE ");
        chooseLabel.setFont(font(F_PIXEL, 10));
        chooseLabel.setStyle("-fx-text-fill: " + GOLD + ";");

        HBox sideCards = buildSideCards();

        Label errorLbl = new Label();
        errorLbl.setFont(font(F_INTER, 12));
        errorLbl.setStyle("-fx-text-fill: #e74c3c;");

        Button startBtn = buildEnterButton(errorLbl);

        this.getChildren().addAll(title, titleSub, howToPlay, chooseLabel, sideCards, startBtn, errorLbl);
    }

    private VBox buildInstructionsBox() {
        Label header = new Label("HOW  TO  PLAY");
        header.setFont(font(F_PIXEL, 10));
        header.setStyle("-fx-text-fill: " + GOLD + ";");

        String[][] tips = {
            {"★", "Select SCARER or LAUGHER before entering."},
            {"◆", "Each turn: optionally power up, then roll to move."},
            {"▣", "Doors grant or drain energy based on your role."},
            {"»", "Conveyor Belts push forward; Socks drag you back."},
            {"♦", "Card Cells draw a mystery card — boon or curse!"},
            {"♟", "Monster Cells: same role = power-up; opposite = swap!"},
            {"♛", "Reach Cell 99 with ≥ 1000 energy to win!"},
            {"◈", "Shields block energy loss — use them wisely."},
        };

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(7);
        grid.setAlignment(Pos.CENTER);

        for (int i = 0; i < tips.length; i++) {
            grid.add(buildTipCard(tips[i][0], tips[i][1]), i % 2, i / 2);
        }

        VBox box = new VBox(10, header, grid);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(14, 20, 14, 20));
        box.setMaxWidth(760);
        box.setStyle(
            "-fx-background-color: rgba(108,52,131,0.13);" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: rgba(155,89,182,0.40);" +
            "-fx-border-radius: 14;" +
            "-fx-border-width: 1.5;"
        );
        return box;
    }

    private HBox buildTipCard(String icon, String text) {
        Label iconLbl = new Label(icon);
        iconLbl.setFont(font(F_INTER, 13));
        iconLbl.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold;");
        iconLbl.setMinWidth(22);
        iconLbl.setAlignment(Pos.CENTER);

        Label textLbl = new Label(text);
        textLbl.setFont(font(F_INTER, 11));
        textLbl.setStyle("-fx-text-fill: " + TEXT_MAIN + ";");
        textLbl.setWrapText(true);
        textLbl.setMaxWidth(310);

        HBox card = new HBox(8, iconLbl, textLbl);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(6, 10, 6, 10));
        card.setPrefWidth(355);
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04);" +
            "-fx-background-radius: 8;"
        );
        return card;
    }

    private HBox buildSideCards() {
        VBox scarerCard  = buildRoleCard("SCARER",
            "Masters of fear. Drain opponents\nwith every step you take.",
            SCARER_BLUE, "scarer");

        VBox laugherCard = buildRoleCard("LAUGHER",
            "Joy is your weapon. Leave rivals\nlaughing and powerless.",
            LAUGHER_GRN, "laugher");

        scarerCard.setOnMouseClicked(e -> {
            selectedRole = "SCARER";
            applySelectedStyle(scarerCard, SCARER_BLUE);
            applyDeselectedStyle(laugherCard);
        });
        laugherCard.setOnMouseClicked(e -> {
            selectedRole = "LAUGHER";
            applySelectedStyle(laugherCard, LAUGHER_GRN);
            applyDeselectedStyle(scarerCard);
        });

        HBox row = new HBox(30, scarerCard, laugherCard);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private VBox buildRoleCard(String roleName, String desc, String accentColor, String imgKey) {
        StackPane imageArea = new StackPane();
        imageArea.setPrefSize(90, 90);
        imageArea.setStyle(
            "-fx-background-radius: 45;" +
            "-fx-background-color: " + accentColor + "33;"
        );

        Image img = ResourceLoader.loadImage(imgKey, 180, 180);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(90);
            iv.setFitHeight(90);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setClip(new Circle(45, 45, 45));
            iv.setEffect(new DropShadow(14, Color.web(accentColor, 0.55)));
            imageArea.getChildren().add(iv);
        } else {
            Label fb = new Label(roleName.substring(0, 1));
            fb.setFont(Font.font(52));
            fb.setEffect(new DropShadow(20, Color.web(accentColor, 0.65)));
            imageArea.getChildren().add(fb);
        }

        Label nameLabel = new Label(roleName);
        nameLabel.setFont(font(F_BANGERS, 26));
        nameLabel.setStyle("-fx-text-fill: white;");

        Label descLabel = new Label(desc);
        descLabel.setFont(font(F_INTER, 11));
        descLabel.setStyle("-fx-text-fill: " + TEXT_DIM + "; -fx-text-alignment: center;");
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(170);

        Label tick = new Label("SELECTED");
        tick.setFont(font(F_PIXEL, 7));
        tick.setStyle("-fx-text-fill: " + accentColor + ";");
        tick.setVisible(false);
        tick.setId("tick");

        VBox card = new VBox(8, imageArea, nameLabel, descLabel, tick);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16, 20, 16, 20));
        card.setPrefWidth(200);
        card.setStyle(
            "-fx-background-color: " + BG_MID + ";" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );

        card.setOnMouseEntered(e -> { if (!isSelected(card)) card.setStyle(card.getStyle().replace(BG_MID, "#1f1f38")); });
        card.setOnMouseExited(e  -> { if (!isSelected(card)) applyDeselectedStyle(card); });

        return card;
    }

    private boolean isSelected(VBox card) {
        return card.getStyle().contains("border-color");
    }

    private void applySelectedStyle(VBox card, String color) {
        card.setStyle(
            "-fx-background-color: " + color + "1C;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: " + color + "BB;" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 2;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + color + ",12,0.25,0,0);"
        );
        card.getChildren().stream()
            .filter(n -> "tick".equals(n.getId()))
            .findFirst()
            .ifPresent(n -> n.setVisible(true));
    }

    private void applyDeselectedStyle(VBox card) {
        card.setStyle(
            "-fx-background-color: " + BG_MID + ";" +
            "-fx-background-radius: 16;" +
            "-fx-cursor: hand;"
        );
        card.getChildren().stream()
            .filter(n -> "tick".equals(n.getId()))
            .findFirst()
            .ifPresent(n -> n.setVisible(false));
    }

    private Button buildEnterButton(Label errorLbl) {
        Button btn = new Button("START");
        btn.setFont(font(F_PIXEL, 11));
        btn.setPrefSize(270, 46);
        applyBtnStyle(btn, RED_BTN);

        btn.setOnMouseEntered(e -> applyBtnStyle(btn, RED_HOVER));
        btn.setOnMouseExited(e  -> applyBtnStyle(btn, RED_BTN));

        btn.setOnAction(e -> {
            if (selectedRole == null) {
                showChooseSidePopup();
                return;
            }
            try {
                ViewManager.updateView(new InstructionsView(selectedRole));
            } catch (Exception ex) {
                errorLbl.setText("Failed to load: " + ex.getMessage());
            }
        });
        return btn;
    }

    private void showChooseSidePopup() {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Choose Your Side");
        popup.setResizable(false);

        ImageView bg = new ImageView();
        Image bgImg = ResourceLoader.loadImage("start_popup", 420, 260);
        if (bgImg != null) {
            bg.setImage(bgImg);
            bg.setFitWidth(420);
            bg.setFitHeight(260);
            bg.setPreserveRatio(false);
            bg.setOpacity(0.25);
        }

        Label title = new Label("CHOOSE YOUR SIDE!");
        title.setFont(font(F_BANGERS, 28));
        title.setStyle("-fx-text-fill: " + GOLD + ";");

        Label msg = new Label("You must pick SCARER or LAUGHER\nbefore entering the Floor.");
        msg.setFont(font(F_INTER, 13));
        msg.setStyle("-fx-text-fill: #ecf0f1;");
        msg.setAlignment(Pos.CENTER);
        msg.setTextAlignment(TextAlignment.CENTER);

        Button okBtn = new Button("GOT IT");
        okBtn.setFont(font(F_PIXEL, 10));
        okBtn.setPrefSize(160, 42);
        applyBtnStyle(okBtn, RED_BTN);
        okBtn.setOnMouseEntered(e -> applyBtnStyle(okBtn, RED_HOVER));
        okBtn.setOnMouseExited(e  -> applyBtnStyle(okBtn, RED_BTN));
        okBtn.setOnAction(e -> popup.close());

        VBox content = new VBox(14, title, msg, okBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));

        StackPane root = new StackPane(bg, content);
        root.setStyle(
            "-fx-background-color: #0d0d1a;" +
            "-fx-background-radius: 16;" +
            "-fx-border-color: rgba(155,89,182,0.60);" +
            "-fx-border-radius: 16;" +
            "-fx-border-width: 2;"
        );

        Scene scene = new Scene(root, 420, 260);
        scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void applyBtnStyle(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian," + color + ",14,0.5,0,0);"
        );
    }

    private Font font(String path, double size) {
        return ResourceLoader.font(path, size);
    }
}

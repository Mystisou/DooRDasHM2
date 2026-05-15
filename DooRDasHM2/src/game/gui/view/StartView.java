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
import javafx.scene.shape.Rectangle;
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

    private VBox   scarerCard;
    private VBox   laugherCard;
    private Button startBtn;
    private Label  errorLbl;

    public StartView() {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(18);
        this.setPadding(new Insets(32, 60, 32, 60));
        this.setStyle("-fx-background-color: " + BG_DARK + ";");
        build();
    }

    public VBox   getScarerCard()  { 
    	return scarerCard;  }
    public VBox   getLaugherCard() { 
    	return laugherCard; }
    public Button getStartButton() { 
    	return startBtn;    }

    public void selectScarer()  { 
    	applySelectedStyle(scarerCard,  SCARER_BLUE); 
    	applyDeselectedStyle(laugherCard); 
    	}
    public void selectLaugher() { 
    	applySelectedStyle(laugherCard, LAUGHER_GRN);
    	applyDeselectedStyle(scarerCard);  
    	}
    public void showError(String message) { 
    	errorLbl.setText(message); }

    public void showNoRolePopup() {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setResizable(false);
        
        ImageView bg = new ImageView();
        Image bgImg  = ResourceLoader.loadImage("start_popup", 420, 260);
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
        okBtn.setOnMouseExited(e -> applyBtnStyle(okBtn, RED_BTN)); 
        okBtn.setOnAction(e -> popup.close());
        
        VBox content = new VBox(14, title, msg, okBtn); 
        content.setAlignment(Pos.CENTER); 
        content.setPadding(new Insets(30));
        
        StackPane root = new StackPane(bg, content);
        root.setStyle("-fx-background-color: #0d0d1a; "
        		+ "-fx-background-radius: 16; "
        		+ "-fx-border-color: rgba(155,89,182,0.60); "
        		+ "-fx-border-radius: 16; -fx-border-width: 2;");
        
        Scene scene = new Scene(root, 420, 260); 
        scene.setFill(Color.TRANSPARENT); 
        popup.setScene(scene); 
        popup.showAndWait();
    }

    // ── main layout ──────────────────────────────────────────────────────────

    private void build() {
        Label title = new Label("DooR DasH");
        title.setFont(font(F_BANGERS, 64));
        title.setStyle("-fx-text-fill: " + GOLD + "; -fx-effect: dropshadow(gaussian,#6c3483,16,0.6,0,0);");

        Label titleSub = new Label("Scare  vs  Laugh  Touchdown");
        titleSub.setFont(font(F_PIXEL, 10));
        titleSub.setStyle("-fx-text-fill: " + PURPLE_LIGHT + ";");

        Label chooseLabel = new Label(" CHOOSE YOUR SIDE ");
        chooseLabel.setFont(font(F_PIXEL, 10));
        chooseLabel.setStyle("-fx-text-fill: " + GOLD + ";");

        errorLbl = new Label();
        errorLbl.setFont(font(F_INTER, 12));
        errorLbl.setStyle("-fx-text-fill: #e74c3c;");

        startBtn = new Button("START");
        startBtn.setFont(font(F_PIXEL, 11));
        startBtn.setPrefSize(270, 46);
        applyBtnStyle(startBtn, RED_BTN);
        startBtn.setOnMouseEntered(e -> applyBtnStyle(startBtn, RED_HOVER));
        startBtn.setOnMouseExited(e  -> applyBtnStyle(startBtn, RED_BTN));

        this.getChildren().addAll(title, titleSub, buildDescriptionBox(), chooseLabel, buildSideCards(), startBtn, errorLbl);
    }

    private VBox buildDescriptionBox() {
        Label desc = new Label(
            "Pick a side, roll the dice, and run for Boo's Door.\n" +
            "Grab energy, survive the Floor's hazards, and outwit your opponent — " +
            "first one there with enough power wins."
        );
        desc.setFont(font(F_INTER, 13));
        desc.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        desc.setAlignment(Pos.CENTER);
        desc.setTextAlignment(TextAlignment.CENTER);
        desc.setWrapText(true);
        desc.setMaxWidth(580);

        String ghostStyle = "-fx-background-color: transparent; -fx-text-fill: " + GOLD + "; -fx-border-color: " + GOLD + "77; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-cursor: hand; -fx-padding: 6 20;";
        String ghostHover = "-fx-background-color: rgba(241,196,15,0.10); -fx-text-fill: " + GOLD + "; -fx-border-color: " + GOLD + "; -fx-border-radius: 8; -fx-border-width: 1.5; -fx-cursor: hand; -fx-padding: 6 20;";
        Button howToBtn = new Button("View How to Play");
        howToBtn.setFont(font(F_INTER, 12));
        howToBtn.setPrefHeight(36);
        howToBtn.setStyle(ghostStyle);
        howToBtn.setOnMouseEntered(e -> howToBtn.setStyle(ghostHover));
        howToBtn.setOnMouseExited(e  -> howToBtn.setStyle(ghostStyle));
        howToBtn.setOnAction(e -> showHowToPlayPopup());

        VBox box = new VBox(10, desc, howToBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(14, 20, 14, 20));
        box.setMaxWidth(680);
        box.setStyle("-fx-background-color: rgba(108,52,131,0.10); -fx-background-radius: 14; -fx-border-color: rgba(155,89,182,0.28); -fx-border-radius: 14; -fx-border-width: 1;");
        return box;
    }

    // ── how to play popup ────────────────────────────────────────────────────

    private void showHowToPlayPopup() {
        Stage popup = new Stage();
        popup.initStyle(StageStyle.TRANSPARENT);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setResizable(false);

        Label bookIcon = new Label("📖"); bookIcon.setStyle("-fx-font-size: 32px;");
        Label hdr = new Label("HOW  TO  PLAY"); hdr.setFont(font(F_BANGERS, 32)); hdr.setStyle("-fx-text-fill: " + GOLD + ";");
        Label subHdr = new Label("Everything you need before stepping onto the Floor");
        subHdr.setFont(font(F_INTER, 12)); subHdr.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        VBox header = new VBox(4, bookIcon, hdr, subHdr); header.setAlignment(Pos.CENTER); header.setPadding(new Insets(0, 0, 10, 0));

        VBox content = new VBox(14);
        content.setPadding(new Insets(4, 8, 4, 8));
        content.getChildren().addAll(
            ruleSection("The Goal",
                "Race to Cell 99 with at least 1,000 energy. Sounds simple. It's not."),
            ruleSection("How a Turn Works",
                "You can spend 500 energy to trigger your powerup before rolling. " +
                "Then roll and move. If your landing spot is taken by your opponent the move is blocked — roll again. " +
                "Land on a cell and whatever's there happens to you."),
            ruleSection("Doors",
                "Half the board is doors. Land on one that matches your role and your whole team gains energy. " +
                "Land on the wrong one and everyone on your team loses it. " +
                "A shield blocks the loss. Each door only activates once."),
            ruleSection("Monster Cells",
                "Six cells have a monster on them. Same role as you? Your powerup fires for free. " +
                "Opposite role and you have more energy? Your energies swap. Less energy? Nothing happens."),
            ruleSection("Conveyor Belts",
                "Step on one and you jump forward. The cell you land on after the jump doesn't activate."),
            ruleSection("Contamination Socks",
                "You get dragged backwards and lose 100 energy. A shield can save the energy but you still move back."),
            buildCardCellsSection(),
            buildMonsterTypesSection(),
            ruleSection("Shields",
                "Protects your whole team from the next energy hit. " +
                "Only one shield at a time — if your opponent has it, they lose it when you draw yours. " +
                "Schemer's steal ignores shields."),
            ruleSection("Confusion",
                "Your role flips temporarily. Wrong doors now hurt you. Wears off after a few turns."),
            ruleSection("Winning",
                "Hit Cell 99 with 1,000 or more energy. Get there short and you keep playing.")
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPrefHeight(490);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        Button gotItBtn = new Button("GOT IT!");
        gotItBtn.setFont(font(F_PIXEL, 10)); gotItBtn.setPrefSize(200, 44);
        applyBtnStyle(gotItBtn, RED_BTN);
        gotItBtn.setOnMouseEntered(e -> applyBtnStyle(gotItBtn, RED_HOVER));
        gotItBtn.setOnMouseExited(e  -> applyBtnStyle(gotItBtn, RED_BTN));
        gotItBtn.setOnAction(e -> popup.close());

        HBox btnRow = new HBox(gotItBtn); btnRow.setAlignment(Pos.CENTER); btnRow.setPadding(new Insets(14, 0, 4, 0));
        Separator sep = new Separator(); sep.setStyle("-fx-background-color: rgba(155,89,182,0.30);");

        VBox outer = new VBox(10, header, sep, scroll, btnRow);
        outer.setPadding(new Insets(28, 28, 24, 28));

        Rectangle clip = new Rectangle(780, 700); clip.setArcWidth(20); clip.setArcHeight(20);
        StackPane root = new StackPane(outer); root.setClip(clip);
        root.setStyle("-fx-background-color: #0d0d1a; -fx-background-radius: 16; -fx-border-color: rgba(155,89,182,0.55); -fx-border-radius: 16; -fx-border-width: 2; -fx-effect: dropshadow(gaussian,#6c3483,28,0.35,0,0);");

        Scene scene = new Scene(root, 780, 700); scene.setFill(Color.TRANSPARENT);
        popup.setScene(scene); popup.showAndWait();
    }

 // ── monster types section — 2x2 grid, two overlapping images per type ────────

    private VBox buildMonsterTypesSection() {
        Label titleLbl = new Label("Monster Types");
        titleLbl.setFont(font(F_BANGERS, 20));
        titleLbl.setStyle("-fx-text-fill: " + GOLD + ";");

        Label intro = new Label(
            "Your monster's type changes how it moves and collects energy. " +
            "Each one has a passive trait and a powerup you can trigger mid-game."
        );
        intro.setFont(font(F_INTER, 12));
        intro.setStyle("-fx-text-fill: " + TEXT_MAIN + ";");
        intro.setWrapText(true);

        // Two monsters per type:
        //   Dasher      → Mike_Wazowski  + Fungus
        //   Dynamo      → James_Sullivan + Yeti
        //   Multitasker → Celia_Mae      + Roz
        //   Schemer     → Randall_Boggs  + Henry_Waternoose
        String[][] monsters = {
            { "Mike_Wazowski",  "Fungus",            "#00bcd4", "Dasher",
              "Moves at 2x speed. Powerup pushes that to 3x for 3 turns." },
            { "James_Sullivan", "Yeti",               "#2ecc71", "Dynamo",
              "Double energy gains AND losses. Powerup freezes your opponent for a whole turn." },
            { "Celia_Mae",      "Roz",                "#9b59b6", "Multitasker",
              "Slower movement but +200 on every energy change. Powerup restores normal speed for 2 turns." },
            { "Randall_Boggs",  "Henry_Waternoose",   "#e67e22", "Schemer",
              "Every energy shift gets +10. Powerup steals from every monster on the board." },
        };

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10, 0, 4, 0));

        for (int i = 0; i < monsters.length; i++) {
            grid.add(
                buildMonsterCard(
                    monsters[i][0], // front image (left / in front)
                    monsters[i][1], // back image  (right / behind)
                    monsters[i][2], // accent colour
                    monsters[i][3], // type name
                    monsters[i][4]  // description
                ),
                i % 2, i / 2
            );
        }

        VBox section = new VBox(8, titleLbl, intro, grid);
        section.setPadding(new Insets(10, 14, 12, 14));
        section.setStyle(
            "-fx-background-color: rgba(255,255,255,0.04);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(155,89,182,0.20);" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        return section;
    }

    /**
     * One monster-type card.
     * frontKey / backKey are image resource names — the front image sits on the
     * left and is rendered on top; the back image peeks out from the right side.
     */
    private VBox buildMonsterCard(String frontKey, String backKey,
                                   String accentColor, String name, String desc) {

        Pane imgOverlap = buildOverlappingImages(frontKey, backKey, accentColor);

        Label nameLbl = new Label(name);
        nameLbl.setFont(font(F_BANGERS, 18));
        nameLbl.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold;");
        nameLbl.setAlignment(Pos.CENTER);

        Label descLbl = new Label(desc);
        descLbl.setFont(font(F_INTER, 11));
        descLbl.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(300);
        descLbl.setAlignment(Pos.CENTER);
        descLbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        VBox card = new VBox(8, imgOverlap, nameLbl, descLbl);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(12, 14, 12, 14));
        card.setPrefWidth(320);
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.03);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + accentColor + "44;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;"
        );
        return card;
    }

    /**
     * Builds a Pane containing two overlapping circular images.
     * The front image is on the LEFT and rendered on top.
     * The back image peeks out from the RIGHT, slightly behind.
     *
     *   [ front ]
     *        [ back ]   ← shifted right by OFFSET px
     */
    private Pane buildOverlappingImages(String frontKey, String backKey,
                                                             String accentColor) {
        final int IMG_SIZE = 74;   // diameter of each circle
        final int OFFSET   = 54;   // how far the back image is shifted to the right

        Pane pane = new Pane();
        pane.setPrefSize(IMG_SIZE + OFFSET, IMG_SIZE);
        pane.setMaxSize(IMG_SIZE + OFFSET, IMG_SIZE);
        pane.setMinSize(IMG_SIZE + OFFSET, IMG_SIZE);

        // back image — right side, rendered first so it sits behind
        StackPane backPane = makeSingleCircleImage(backKey, IMG_SIZE, accentColor, 0.55);
        backPane.setLayoutX(OFFSET);
        backPane.setLayoutY(0);

        // front image — left side, rendered last so it sits in front
        StackPane frontPane = makeSingleCircleImage(frontKey, IMG_SIZE, accentColor, 0.90);
        frontPane.setLayoutX(0);
        frontPane.setLayoutY(0);

        pane.getChildren().addAll(backPane, frontPane);
        return pane;
    }

    /**
     * A single circular image with a glowing coloured border.
     * borderAlpha controls the border opacity so the back image looks further away.
     */
    private StackPane makeSingleCircleImage(String imgKey, int size, String accentColor,
                                             double borderAlpha) {
        int radius = size / 2;

        StackPane pane = new StackPane();
        pane.setPrefSize(size, size);
        pane.setMaxSize(size, size);

        Image img = ResourceLoader.loadImage(imgKey, size, size);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(size);
            iv.setFitHeight(size);
            iv.setPreserveRatio(false);
            iv.setClip(new Circle(radius, radius, radius));
            pane.getChildren().add(iv);
        } else {
            Circle bg = new Circle(radius);
            bg.setFill(Color.web(accentColor, 0.25));
            pane.getChildren().add(bg);
        }

        // glowing border ring drawn on top of the image
        Circle border = new Circle(radius);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.web(accentColor, borderAlpha));
        border.setStrokeWidth(2.5);
        border.setEffect(new DropShadow(12, Color.web(accentColor, borderAlpha * 0.75)));
        pane.getChildren().add(border);

        return pane;
    }

    // ── card cells section — 3x3 grid ────────────────────────────────────────

    private VBox buildCardCellsSection() {
        Label titleLbl = new Label("Card Cells");
        titleLbl.setFont(font(F_BANGERS, 20)); titleLbl.setStyle("-fx-text-fill: " + GOLD + ";");
        Label intro = new Label("Land on a card cell and draw from the shuffled pile. The effect hits both players — lucky or not depends on the draw.");
        intro.setFont(font(F_INTER, 12)); intro.setStyle("-fx-text-fill: " + TEXT_MAIN + ";"); intro.setWrapText(true);

        // Suggested images from your existing assets:
        //   scarer_door → Position Swap   |  sock → start-over cards
        //   energy      → steal cards     |  shield → Super Shield
        //   confusion   → confusion cards
        String[][] cards = {
            { "scarer_door", "#3498db", "Position Swap",      "Swap places (only if you're behind)"  },
            { "sock",        "#e74c3c", "Contamination Code", "You go back to Cell 0"                 },
            { "sock",        "#e74c3c", "2319 Alert",         "Opponent goes back to Cell 0"          },
            { "energy",      "#f39c12", "Small Snatcher",     "Steal 50 energy"                      },
            { "energy",      "#f39c12", "Sneaky Thief",       "Steal 100 energy"                     },
            { "energy",      "#f39c12", "Mega Drain",         "Steal 150 energy"                     },
            { "shield",      "#2ecc71", "Super Shield",       "Block next energy loss for your team"  },
            { "confusion",   "#9b59b6", "Mind Scramble",      "Both confused for 2 turns"             },
            { "confusion",   "#9b59b6", "Total Confusion",    "Both confused for 3 turns"             },
        };

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(12); grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10, 0, 4, 0));
        for (int i = 0; i < cards.length; i++)
            grid.add(buildCardTile(cards[i][0], cards[i][1], cards[i][2], cards[i][3]), i % 3, i / 3);

        VBox section = new VBox(8, titleLbl, intro, grid);
        section.setPadding(new Insets(10, 14, 12, 14));
        section.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10; -fx-border-color: rgba(155,89,182,0.20); -fx-border-radius: 10; -fx-border-width: 1;");
        return section;
    }

    private VBox buildCardTile(String imgKey, String accentColor, String name, String effect) {
        StackPane imgPane = new StackPane();
        imgPane.setPrefSize(68, 68); imgPane.setMaxSize(68, 68);
        Circle border = new Circle(34);
        border.setFill(Color.TRANSPARENT); border.setStroke(Color.web(accentColor)); border.setStrokeWidth(2);
        border.setEffect(new DropShadow(10, Color.web(accentColor, 0.60)));
        Image img = ResourceLoader.loadImage(imgKey, 62, 62);
        if (img != null) {
            ImageView iv = new ImageView(img); iv.setFitWidth(62); iv.setFitHeight(62); iv.setPreserveRatio(false);
            iv.setClip(new Circle(31, 31, 31));
            imgPane.getChildren().addAll(iv, border);
        } else {
            Circle bg = new Circle(31); bg.setFill(Color.web(accentColor, 0.25));
            imgPane.getChildren().addAll(bg, border);
        }
        Label nameLbl = new Label(name); nameLbl.setFont(font(F_BANGERS, 14));
        nameLbl.setStyle("-fx-text-fill: " + GOLD + "; -fx-font-weight: bold;");
        nameLbl.setAlignment(Pos.CENTER); nameLbl.setWrapText(true); nameLbl.setMaxWidth(180); nameLbl.setTextAlignment(TextAlignment.CENTER);
        Label effectLbl = new Label(effect); effectLbl.setFont(font(F_INTER, 10)); effectLbl.setStyle("-fx-text-fill: " + TEXT_DIM + ";");
        effectLbl.setWrapText(true); effectLbl.setMaxWidth(180); effectLbl.setAlignment(Pos.CENTER); effectLbl.setTextAlignment(TextAlignment.CENTER);
        VBox tile = new VBox(5, imgPane, nameLbl, effectLbl); tile.setAlignment(Pos.TOP_CENTER);
        tile.setPadding(new Insets(10)); tile.setPrefWidth(200);
        tile.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 10; -fx-border-color: " + accentColor + "33; -fx-border-radius: 10; -fx-border-width: 1;");
        return tile;
    }

    // ── generic rule section ─────────────────────────────────────────────────

    private VBox ruleSection(String title, String body) {
        Label titleLbl = new Label(title); titleLbl.setFont(font(F_BANGERS, 20)); titleLbl.setStyle("-fx-text-fill: " + GOLD + ";");
        Label bodyLbl  = new Label(body);  bodyLbl.setFont(font(F_INTER, 12));    bodyLbl.setStyle("-fx-text-fill: " + TEXT_MAIN + "; -fx-line-spacing: 2;");
        bodyLbl.setWrapText(true); bodyLbl.setMaxWidth(Double.MAX_VALUE);
        VBox section = new VBox(5, titleLbl, bodyLbl);
        section.setPadding(new Insets(10, 14, 10, 14));
        section.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 10; -fx-border-color: rgba(155,89,182,0.20); -fx-border-radius: 10; -fx-border-width: 1;");
        return section;
    }

    // ── side cards ────────────────────────────────────────────────────────────

    private HBox buildSideCards() {
        scarerCard  = buildRoleCard("SCARER",  "Masters of fear. Drain opponents\nwith every step you take.", SCARER_BLUE, "scarer");
        laugherCard = buildRoleCard("LAUGHER", "Joy is your weapon. Leave rivals\nlaughing and powerless.",  LAUGHER_GRN, "laugher");
        scarerCard .setOnMouseEntered(e -> { if (!isSelected(scarerCard))  scarerCard .setStyle(scarerCard .getStyle().replace(BG_MID, "#1f1f38")); });
        scarerCard .setOnMouseExited(e  -> { if (!isSelected(scarerCard))  applyDeselectedStyle(scarerCard);  });
        laugherCard.setOnMouseEntered(e -> { if (!isSelected(laugherCard)) laugherCard.setStyle(laugherCard.getStyle().replace(BG_MID, "#1f1f38")); });
        laugherCard.setOnMouseExited(e  -> { if (!isSelected(laugherCard)) applyDeselectedStyle(laugherCard); });
        HBox row = new HBox(30, scarerCard, laugherCard); row.setAlignment(Pos.CENTER);
        return row;
    }

    private VBox buildRoleCard(String roleName, String desc, String accentColor, String imgKey) {
        StackPane imageArea = new StackPane(); imageArea.setPrefSize(90, 90);
        imageArea.setStyle("-fx-background-radius: 45; -fx-background-color: " + accentColor + "33;");
        Image img = ResourceLoader.loadImage(imgKey, 180, 180);
        if (img != null) {
            ImageView iv = new ImageView(img); iv.setFitWidth(90); iv.setFitHeight(90); iv.setPreserveRatio(true); iv.setSmooth(true);
            iv.setClip(new Circle(45, 45, 45)); iv.setEffect(new DropShadow(14, Color.web(accentColor, 0.55))); imageArea.getChildren().add(iv);
        } else { Label fb = new Label(roleName.substring(0,1)); fb.setFont(Font.font(52)); fb.setEffect(new DropShadow(20, Color.web(accentColor, 0.65))); imageArea.getChildren().add(fb); }
        Label nameLabel = new Label(roleName); nameLabel.setFont(font(F_BANGERS, 26)); nameLabel.setStyle("-fx-text-fill: white;");
        Label descLabel = new Label(desc);
        descLabel.setFont(font(F_INTER, 11));
        descLabel.setStyle("-fx-text-fill: " + TEXT_DIM + "; -fx-text-alignment: center;");
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(170);      

        Label tick = new Label("SELECTED"); tick.setFont(font(F_PIXEL, 7)); tick.setStyle("-fx-text-fill: " + accentColor + ";"); tick.setVisible(false); tick.setId("tick");
        VBox card = new VBox(8, imageArea, nameLabel, descLabel, tick); card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16, 20, 16, 20)); card.setPrefWidth(200);
        card.setStyle("-fx-background-color: " + BG_MID + "; -fx-background-radius: 16; -fx-cursor: hand;");
        return card;
    }

    private boolean isSelected(VBox card) { return card.getStyle().contains("border-color"); }

    private void applySelectedStyle(VBox card, String color) {
        card.setStyle("-fx-background-color: " + color + "1C; -fx-background-radius: 16; -fx-border-color: " + color + "BB; -fx-border-radius: 16; -fx-border-width: 2; -fx-cursor: hand; -fx-effect: dropshadow(gaussian," + color + ",12,0.25,0,0);");
        card.getChildren().stream().filter(n -> "tick".equals(n.getId())).findFirst().ifPresent(n -> n.setVisible(true));
    }

    private void applyDeselectedStyle(VBox card) {
        card.setStyle("-fx-background-color: " + BG_MID + "; -fx-background-radius: 16; -fx-cursor: hand;");
        card.getChildren().stream().filter(n -> "tick".equals(n.getId())).findFirst().ifPresent(n -> n.setVisible(false));
    }

    private void applyBtnStyle(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand; -fx-effect: dropshadow(gaussian," + color + ",14,0.5,0,0);");
    }

    private Font font(String path, double size) { return ResourceLoader.font(path, size); }
}
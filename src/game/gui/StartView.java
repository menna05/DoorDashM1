package game.gui;

import game.engine.Role;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class StartView {
    private StackPane root; // Outer responsive container
    private BorderPane baseLayout; // Fixed 1100x750 internal canvas
    private GameController controller;
    private Role selectedRole = null;
    private Button startBtn;

    public StartView(GameController controller) {
        this.controller = controller;
        buildUI();
    }

    public StackPane getRoot() {
        return root;
    }

    private void buildUI() {
        root = new StackPane();
        root.setStyle("-fx-background-color: #0f2027;"); // Fills excess ultra-wide screen space smoothly

        // 1. Build the fixed base layout exactly as designed
        baseLayout = new BorderPane();
        baseLayout.setPrefSize(1100, 750);
        baseLayout.setMaxSize(1100, 750);
        baseLayout.setMinSize(1100, 750);
        baseLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #0f2027, #203a43, #2c5364);");

        // Top Heading Panel
        VBox topBox = new VBox(8);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(35, 20, 10, 20));

        Text title = new Text("DooR DasH");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 52));
        title.setFill(Color.web("#f59e0b"));
        title.setEffect(new DropShadow(8, Color.BLACK));

        Text subtitle = new Text("Scare vs Laugh Touchdown");
        subtitle.setFont(Font.font("Verdana", FontWeight.SEMI_BOLD, 26));
        subtitle.setFill(Color.WHITE);

        topBox.getChildren().addAll(title, subtitle);
        baseLayout.setTop(topBox);

        // Central Guidance Details & Selection Panels
        VBox centerBox = new VBox(25);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10, 40, 20, 40));

        // Instructions Block
        VBox instructionsBox = new VBox(8);
        instructionsBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.08); -fx-background-radius: 12; -fx-padding: 18;");
        instructionsBox.setMaxWidth(850);

        Label instTitle = new Label("📖 Touchdown Instructions");
        instTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        instTitle.setTextFill(Color.web("#38bdf8"));

        Label instText = new Label(
            "• Ultimate Goal: Race across the 100-cell grid layout to trigger Boo's Target Door (Cell 99) carrying at least 1000 Energy Reserves.\n" +
            "• Turn Order: Decide whether to fire your custom Powerup ability (costs 500 Energy), then Roll the Dice to advance your position.\n" +
            "• Grid Tiles: Stepping on aligned Doors grants team-wide energy. Avoid volatile Contamination Socks (-100 Energy penalty)!\n" +
            "• Mystery Cards: Landing on Card tiles applies random global state effects (Siphoning, Protection, Absolute Positional Swaps, or Role Confusion).\n" +
            "• Exception Handling: If an invalid destination square is targeted, the system halts the step cleanly and lets you reroll safely."
        );
        instText.setFont(Font.font("Arial", 14));
        instText.setTextFill(Color.web("#f1f5f9"));
        instText.setWrapText(true);
        instText.setLineSpacing(4);

        instructionsBox.getChildren().addAll(instTitle, instText);

        // Side Choosing Block
        Label selectTitle = new Label("Select Your Operational Alliance");
        selectTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        selectTitle.setTextFill(Color.WHITE);

        HBox cardsBox = new HBox(35);
        cardsBox.setAlignment(Pos.CENTER);

        VBox scarerCard = createSideCard("SCARER", "🔴", "Harness the visceral collection methods of scream dynamics!");
        VBox laugherCard = createSideCard("LAUGHER", "🟢", "Revolutionize production pipelines utilizing pure high-yield laughter!");

        scarerCard.setOnMouseClicked(e -> {
            selectedRole = Role.SCARER;
            scarerCard.setStyle("-fx-background-color: #b91c1c; -fx-background-radius: 16; -fx-border-color: #f59e0b; -fx-border-width: 3; -fx-border-radius: 16;");
            laugherCard.setStyle("-fx-background-color: rgba(22, 101, 52, 0.3); -fx-background-radius: 16;");
            startBtn.setDisable(false);
        });

        laugherCard.setOnMouseClicked(e -> {
            selectedRole = Role.LAUGHER;
            laugherCard.setStyle("-fx-background-color: #15803d; -fx-background-radius: 16; -fx-border-color: #f59e0b; -fx-border-width: 3; -fx-border-radius: 16;");
            scarerCard.setStyle("-fx-background-color: rgba(185, 28, 28, 0.3); -fx-background-radius: 16;");
            startBtn.setDisable(false);
        });

        cardsBox.getChildren().addAll(scarerCard, laugherCard);
        centerBox.getChildren().addAll(instructionsBox, selectTitle, cardsBox);
        baseLayout.setCenter(centerBox);

        // Bottom Operations Area
        HBox bottomBox = new HBox();
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10, 20, 40, 20));

        startBtn = new Button("INITIATE FLOOR LAUNCH");
        startBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        startBtn.setStyle("-fx-background-color: #ea580c; -fx-text-fill: white; -fx-background-radius: 25; -fx-padding: 12 45; -fx-cursor: hand;");
        startBtn.setDisable(true);
        startBtn.setEffect(new DropShadow(6, Color.BLACK));
        
        startBtn.setOnAction(e -> controller.startGame(selectedRole));
        bottomBox.getChildren().add(startBtn);
        baseLayout.setBottom(bottomBox);

        // 2. Wrap the fixed layout inside a Group to decouple scaling constraints
        Group scalingWrapper = new Group(baseLayout);
        root.getChildren().add(scalingWrapper);
        StackPane.setAlignment(scalingWrapper, Pos.CENTER);

        // 3. Bind scale transformations directly to screen resizes
        root.widthProperty().addListener((obs, oldV, newV) -> applyScaling());
        root.heightProperty().addListener((obs, oldV, newV) -> applyScaling());
    }

    private void applyScaling() {
        double scaleX = root.getWidth() / 1100.0;
        double scaleY = root.getHeight() / 750.0;
        // Maintain identical relative layout aspect ratios unconditionally
        double scale = Math.min(scaleX, scaleY); 
        
        if (scale > 0) {
            baseLayout.setScaleX(scale);
            baseLayout.setScaleY(scale);
        }
    }

    private VBox createSideCard(String title, String icon, String desc) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPrefSize(260, 180);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-background-radius: 16; -fx-cursor: hand;");
        card.setPadding(new Insets(15));

        Label iconLbl = new Label(icon);
        iconLbl.setFont(Font.font(36));

        Label titleLbl = new Label(title);
        titleLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        titleLbl.setTextFill(Color.WHITE);

        Label descLbl = new Label(desc);
        descLbl.setFont(Font.font("Arial", 13));
        descLbl.setTextFill(Color.web("#cbd5e1"));
        descLbl.setWrapText(true);
        descLbl.setTextAlignment(TextAlignment.CENTER);

        card.getChildren().addAll(iconLbl, titleLbl, descLbl);
        card.setEffect(new DropShadow(8, Color.BLACK));
        return card;
    }
}
package game.gui;
 
import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
 
public class WinView {
 
    private BorderPane root;
    private GameController controller;
    private Monster winningEntity;
    private Monster pEntity;
    private Monster oEntity;
 
    public WinView(GameController controller, Monster winningEntity, Monster pEntity, Monster oEntity) {
        this.controller = controller;
        this.winningEntity = winningEntity;
        this.pEntity = pEntity;
        this.oEntity = oEntity;
        buildUI();
    }
 
    public BorderPane getRoot() {
        return root;
    }
 
    private void buildUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0f172a, #1e293b, #0f172a);");
 
        VBox alignmentContainer = new VBox(22);
        alignmentContainer.setAlignment(Pos.CENTER);
        alignmentContainer.setPadding(new Insets(35));
 
        boolean playerWon = (winningEntity == pEntity);
 
        // FIX: correctly say "GAME WON" vs "GAME OVER" depending on who won
        Label trophyGraphic = new Label(playerWon ? "🏆" : "💀");
        trophyGraphic.setFont(Font.font(72));
        trophyGraphic.setEffect(new DropShadow(12, Color.BLACK));
 
        // FIX: banner text matches the spec — "Game Won" or "Game Over"
        Text mainBanner = new Text(playerWon ? "🎉 GAME WON!" : "💔 GAME OVER");
        mainBanner.setFont(Font.font("Verdana", FontWeight.BOLD, 48));
        mainBanner.setFill(playerWon ? Color.web("#f59e0b") : Color.web("#ef4444"));
        mainBanner.setEffect(new DropShadow(8, Color.BLACK));
 
        // FIX: announce winning monster's name AND original role (as spec requires)
        Label finalWinnerStamp = new Label(
            "🏅 Winner: " + winningEntity.getName() +
            "   |   Role: " + winningEntity.getOriginalRole()
        );
        finalWinnerStamp.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        finalWinnerStamp.setTextFill(Color.WHITE);
        finalWinnerStamp.setWrapText(true);
        finalWinnerStamp.setTextAlignment(TextAlignment.CENTER);
        finalWinnerStamp.setStyle("-fx-background-color: rgba(255,255,255,0.06); -fx-padding: 10 20; -fx-background-radius: 10;");
 
        // Final energy scores
        HBox scoresContainer = new HBox(35);
        scoresContainer.setAlignment(Pos.CENTER);
        scoresContainer.setPadding(new Insets(15));
 
        VBox playerCard = generateScoreboardCard("YOUR MONSTER", pEntity, winningEntity == pEntity);
        VBox opponentCard = generateScoreboardCard("OPPONENT", oEntity, winningEntity == oEntity);
        scoresContainer.getChildren().addAll(playerCard, opponentCard);
 
        // Return to start button
        Button returnBtn = new Button("🔄 Return to Main Menu");
        returnBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        returnBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 30; -fx-cursor: hand;");
        returnBtn.setEffect(new DropShadow(4, Color.BLACK));
        returnBtn.setOnAction(e -> controller.showStartScreen());
 
        alignmentContainer.getChildren().addAll(trophyGraphic, mainBanner, finalWinnerStamp, scoresContainer, returnBtn);
        root.setCenter(alignmentContainer);
    }
 
    private VBox generateScoreboardCard(String blockTitle, Monster monster, boolean isWinner) {
        VBox elementCard = new VBox(8);
        elementCard.setAlignment(Pos.CENTER);
        elementCard.setPrefSize(280, 160);
        elementCard.setPadding(new Insets(15));
 
        if (isWinner) {
            elementCard.setStyle("-fx-background-color: rgba(245,158,11,0.12); -fx-background-radius: 12; -fx-border-color: #f59e0b; -fx-border-width: 2; -fx-border-radius: 12;");
        } else {
            elementCard.setStyle("-fx-background-color: rgba(255,255,255,0.04); -fx-background-radius: 12;");
        }
 
        Label blockStamp = new Label(blockTitle + (isWinner ? "  👑 WINNER" : ""));
        blockStamp.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        blockStamp.setTextFill(isWinner ? Color.web("#f59e0b") : Color.web("#94a3b8"));
 
        Label entName = new Label(monster.getName());
        entName.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        entName.setTextFill(Color.WHITE);
        entName.setWrapText(true);
        entName.setTextAlignment(TextAlignment.CENTER);
 
        // FIX: show original role on win screen (spec requirement)
        Label roleLabel = new Label("Role: " + monster.getOriginalRole());
        roleLabel.setFont(Font.font("Arial", 12));
        roleLabel.setTextFill(Color.web("#94a3b8"));
 
        Label energyLabel = new Label("Final Energy: " + monster.getEnergy());
        energyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        energyLabel.setTextFill(Color.web("#10b981"));
 
        elementCard.getChildren().addAll(blockStamp, entName, roleLabel, energyLabel);
        elementCard.setEffect(new DropShadow(6, Color.BLACK));
        return elementCard;
    }
}
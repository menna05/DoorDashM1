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

        Label trophyGraphic = new Label("🏆");
        trophyGraphic.setFont(Font.font(72));
        trophyGraphic.setEffect(new DropShadow(12, Color.BLACK));

        Text mainBanner = new Text(winningEntity == pEntity ? "TOUCHDOWN SUCCESSFUL!" : "GAME ENDED");
        mainBanner.setFont(Font.font("Verdana", FontWeight.BOLD, 44));
        mainBanner.setFill(winningEntity == pEntity ? Color.web("#f59e0b") : Color.web("#ef4444"));
        mainBanner.setEffect(new DropShadow(8, Color.BLACK));

        Label finalWinnerStamp = new Label("Champion Entity: " + winningEntity.getName() + " (" + winningEntity.getRole() + ")");
        finalWinnerStamp.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        finalWinnerStamp.setTextFill(Color.WHITE);
        finalWinnerStamp.setWrapText(true);
        finalWinnerStamp.setTextAlignment(TextAlignment.CENTER);

        HBox scoresContainer = new HBox(35);
        scoresContainer.setAlignment(Pos.CENTER);
        scoresContainer.setPadding(new Insets(15));

        VBox playerDashboard = generateScoreboardCard("PLAYER ALLIANCE", pEntity.getName(), pEntity.getEnergy(), winningEntity == pEntity);
        VBox opponentDashboard = generateScoreboardCard("OPPONENT RESIDENCE", oEntity.getName(), oEntity.getEnergy(), winningEntity == oEntity);

        scoresContainer.getChildren().addAll(playerDashboard, opponentDashboard);

        Button returnBtn = new Button("🔄 Cycle Main Menu Window");
        returnBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        returnBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 10 30; -fx-cursor: hand;");
        returnBtn.setEffect(new DropShadow(4, Color.BLACK));
        returnBtn.setOnAction(e -> controller.showStartScreen());

        alignmentContainer.getChildren().addAll(trophyGraphic, mainBanner, finalWinnerStamp, scoresContainer, returnBtn);
        root.setCenter(alignmentContainer);
    }

    private VBox generateScoreboardCard(String blockTitle, String monName, int targetReserves, boolean declaresWinner) {
        VBox elementCard = new VBox(8);
        elementCard.setAlignment(Pos.CENTER);
        elementCard.setPrefSize(260, 140);
        elementCard.setPadding(new Insets(12));

        if (declaresWinner) {
            elementCard.setStyle("-fx-background-color: rgba(245, 158, 11, 0.12); -fx-background-radius: 12; -fx-border-color: #f59e0b; -fx-border-width: 2; -fx-border-radius: 12;");
        } else {
            elementCard.setStyle("-fx-background-color: rgba(255, 255, 255, 0.04); -fx-background-radius: 12;");
        }

        Label blockStamp = new Label(blockTitle + (declaresWinner ? " 👑" : ""));
        blockStamp.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        blockStamp.setTextFill(declaresWinner ? Color.web("#f59e0b") : Color.web("#94a3b8"));

        Label entName = new Label(monName);
        entName.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        entName.setTextFill(Color.WHITE);
        entName.setWrapText(true);
        entName.setTextAlignment(TextAlignment.CENTER);

        Label resLabel = new Label("Final Resources: " + targetReserves);
        resLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        resLabel.setTextFill(Color.web("#10b981"));

        elementCard.getChildren().addAll(blockStamp, entName, resLabel);
        elementCard.setEffect(new DropShadow(6, Color.BLACK));
        return elementCard;
    }
}

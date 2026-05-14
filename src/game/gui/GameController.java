package game.gui;

import game.engine.Game;
import game.engine.Role;
import game.engine.Board;
import game.engine.monsters.Monster;
import game.engine.exceptions.*;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;


public class GameController {

    private Stage stage;
    private Game game;

    public GameController(Stage stage) {
        this.stage = stage;
        // Intercept native close requests to guarantee clean shutdown
        this.stage.setOnCloseRequest(e -> System.exit(0));
    }

    public void showStartScreen() {
        StartView startView = new StartView(this);
        Scene scene = new Scene(startView.getRoot(), 1100, 750);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void startGame(Role selectedRole) {
        try {
            // This is the line that was missing! It loads the CSVs and throws IOException if they fail.
            game = new Game(selectedRole); 
            Board.clearLastDrawnCard();
            showBoardScreen();
        } catch (IOException e) { 
            showAlert(Alert.AlertType.ERROR, "Asset Loading Error", 
                "CRITICAL: Failed to load core CSV files. Please ensure cards.csv, cells.csv, and monsters.csv are placed squarely in the root project directory.\nDetails: " + e.getMessage());
        }
    }

    public void showBoardScreen() {
        BoardView boardView = new BoardView(this, game);
        Scene scene = new Scene(boardView.getRoot(), 1350, 880);
        stage.setScene(scene);
        // Ensure content is fully viewable dynamically without manual re-sizing
        stage.setMinWidth(1300);
        stage.setMinHeight(850);
        stage.centerOnScreen();
    }

    public void handleRollDice(BoardView view) {
        Board.clearLastDrawnCard();
        Monster activeMonster = game.getCurrent();
        boolean wasFrozen = activeMonster.isFrozen();

        try {
            game.playTurn();

            if (wasFrozen) {
                view.logMessage("❄️ " + activeMonster.getName() + " was completely frozen! Turn skipped.");
            } else {
                view.logMessage("🎲 " + activeMonster.getName() + " rolled a " + game.getLastRoll() + "!");
            }

            if (Board.getLastDrawnCard() != null) {
                view.showCardDrawn(Board.getLastDrawnCard());
            }

            view.updateAll();
            checkWinner();
        } catch (InvalidMoveException e) {
            // Safe intercept: Landing squarely on the opponent prohibits the move safely
            view.logMessage("⚠️ Invalid Destination: " + activeMonster.getName() + " encountered an occupied tile!");
            showAlert(Alert.AlertType.WARNING, "Move Prohibited", "You cannot land directly on your opponent's space! Please reroll your turn.");
            view.updateAll();
        }
    }

    public void handleUsePowerup(BoardView view) {
        Monster activeMonster = game.getCurrent();
        try {
            game.usePowerup();
            view.logMessage("⚡ " + activeMonster.getName() + " charged their special Powerup!");
            view.updateAll();
            checkWinner();
        } catch (OutOfEnergyException e) {
            showAlert(Alert.AlertType.WARNING, "Insufficient Energy Reserves", e.getMessage());
        }
    }

    private void checkWinner() {
        if (game.getWinner() != null) {
            WinView winView = new WinView(this, game.getWinner(), game.getPlayer(), game.getOpponent());
            Scene scene = new Scene(winView.getRoot(), 1100, 750);
            stage.setScene(scene);
        }
    }

    public void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Closing the popup alert dialog leaves main operation running flawlessly
        alert.showAndWait();
    }
}

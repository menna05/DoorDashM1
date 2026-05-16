package game.gui;
 
import game.engine.Game;
import game.engine.Role;
import game.engine.cards.Card;
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
            game = new Game(selectedRole);
            Board.clearLastDrawnCard();
            showBoardScreen();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Asset Loading Error",
                "Failed to load CSV files. Make sure cards.csv, cells.csv, and monsters.csv are in the project root.\nDetails: " + e.getMessage());
        }
    }
 
    public void showBoardScreen() {
        BoardView boardView = new BoardView(this, game);
        Scene scene = new Scene(boardView.getRoot(), 1350, 880);

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W:
                    game.getPlayer().setPosition(99);
                    boardView.logMessage("🧪 [DEBUG] Player teleported to cell 99.");
                    boardView.updateAll();
                    checkWinner();
                    break;
                case E:
                    game.getPlayer().setEnergy(game.getPlayer().getEnergy() + 500);
                    game.getOpponent().setEnergy(game.getOpponent().getEnergy() + 500);
                    boardView.logMessage("🧪 [DEBUG] +500 energy added to both monsters.");
                    boardView.updateAll();
                    break;
                default:
                    break;
            }
        });

        stage.setScene(scene);
        stage.setMinWidth(1300);
        stage.setMinHeight(850);
        stage.centerOnScreen();
        stage.show();
        scene.getRoot().requestFocus(); // <-- THIS is the fix
    }
    
    public void handleRollDice(BoardView view) {
        Board.clearLastDrawnCard();
        view.hideCard(); // FIX: clear old card display before new turn
 
        Monster activeMonster = game.getCurrent();
        boolean wasFrozen = activeMonster.isFrozen();
 
        // FIX: snapshot energies before the turn so we can show deltas afterward
        view.snapshotEnergies();
 
        // FIX: snapshot shield states before turn so we can detect if shield blocked
        boolean playerShieldBefore = game.getPlayer().isShielded();
        boolean opponentShieldBefore = game.getOpponent().isShielded();
 
        try {
            game.playTurn();
 
            // FIX: increment turn counter after each successful turn
            view.incrementTurn();
 
            if (wasFrozen) {
                // FIX: frozen skip is logged AND shown in action log prominently
                view.logMessage("❄️ " + activeMonster.getName() + " was FROZEN — turn skipped!");
                showAlert(Alert.AlertType.INFORMATION, "Turn Skipped — Frozen!",
                    activeMonster.getName() + " was frozen and their turn was skipped.");
            } else {
                view.updateLastRoll(game.getLastRoll());
                view.logMessage("🎲 " + activeMonster.getName() + " rolled a " + game.getLastRoll() + " and moved to cell " + activeMonster.getPosition() + ".");
            }
 
            // FIX: detect if shield blocked an energy loss and show it on the board
            if (playerShieldBefore && !game.getPlayer().isShielded()) {
                view.logMessage("🛡️ " + game.getPlayer().getName() + "'s shield blocked an energy loss!");
                view.showShieldBlock(game.getPlayer());
            }
            if (opponentShieldBefore && !game.getOpponent().isShielded()) {
                view.logMessage("🛡️ " + game.getOpponent().getName() + "'s shield blocked an energy loss!");
                view.showShieldBlock(game.getOpponent());
            }
 
            if (Board.getLastDrawnCard() != null) {
                Card drawn = Board.getLastDrawnCard();
                view.showCardDrawn(drawn);
                // FIX: log the card effect in the action log too
                view.logMessage("🃏 Card drawn: " + drawn.getName() + " — " + drawn.getDescription());
            }
 
            view.updateAll();
            checkWinner();
 
        } catch (InvalidMoveException e) {
            view.logMessage("⚠️ Invalid move: " + activeMonster.getName() + " cannot land on opponent's cell!");
            showAlert(Alert.AlertType.WARNING, "Move Not Allowed",
                "You cannot land on your opponent's space.\nReason: " + e.getMessage() + "\nPlease roll again.");
            view.updateAll();
        // FIX: catch InvalidTurnException — was completely missing and would crash the app
        } catch (GameActionException e) {
            view.logMessage("⚠️ Game action error: " + e.getMessage());
            showAlert(Alert.AlertType.WARNING, "Action Not Allowed", e.getMessage());
            view.updateAll();
        }
    }
 
    public void handleUsePowerup(BoardView view) {
        Monster activeMonster = game.getCurrent();
 
        // FIX: snapshot energies and shields before powerup for delta display
        view.snapshotEnergies();
        boolean playerShieldBefore = game.getPlayer().isShielded();
        boolean opponentShieldBefore = game.getOpponent().isShielded();
 
        try {
            game.usePowerup();
            view.logMessage("⚡ " + activeMonster.getName() + " used their Powerup!");
 
            // FIX: detect shield blocks during powerup too
            if (playerShieldBefore && !game.getPlayer().isShielded()) {
                view.logMessage("🛡️ " + game.getPlayer().getName() + "'s shield blocked an energy loss!");
                view.showShieldBlock(game.getPlayer());
            }
            if (opponentShieldBefore && !game.getOpponent().isShielded()) {
                view.logMessage("🛡️ " + game.getOpponent().getName() + "'s shield blocked an energy loss!");
                view.showShieldBlock(game.getOpponent());
            }
 
            view.updateAll();
            checkWinner();
 
        } catch (OutOfEnergyException e) {
            showAlert(Alert.AlertType.WARNING, "Not Enough Energy",
                "Cannot use powerup — requires 500 energy.\nCurrent energy: " + activeMonster.getEnergy() + "\nReason: " + e.getMessage());
        // FIX: catch InvalidTurnException here too
        } catch (GameActionException e) {
            showAlert(Alert.AlertType.WARNING, "Powerup Failed", e.getMessage());
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
        // Closing this popup does NOT close or terminate the game
        alert.showAndWait();
    }
}
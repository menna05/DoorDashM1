package game.gui;
 
import game.engine.Game;
import game.engine.Role;
import game.engine.cells.*;
import game.engine.monsters.*;
import game.engine.cards.Card;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import game.engine.cells.Cell;
 
public class BoardView {
    private StackPane root;
    private BorderPane baseLayout;
    private GameController controller;
    private Game game;
 
    private GridPane gridPane;
    private VBox playerDashboard;
    private VBox opponentDashboard;
    private Label currentTurnLbl;
    // FIX: dedicated label for last dice roll result (always visible)
    private Label lastRollLbl;
    // FIX: dedicated label for turn number
    private Label turnCounterLbl;
    private Button powerupBtn;
    private Button rollBtn;
    private ListView<String> actionLog;
    private VBox cardDisplayBox;
    private Label cardNameLbl;
    private Label cardEffectLbl;
 
    private StackPane[] cellPanes = new StackPane[100];
 
    // FIX: track turn count
    private int turnCount = 1;
 
    // FIX: snapshot energy before turn to detect changes
    private int playerEnergyBefore;
    private int opponentEnergyBefore;
 
    public BoardView(GameController controller, Game game) {
        this.controller = controller;
        this.game = game;
        buildUI();
        updateAll();
    }
 
    public StackPane getRoot() {
        return root;
    }
 
    private void buildUI() {
        root = new StackPane();
        root.setStyle("-fx-background-color: #0f172a;");
 
        baseLayout = new BorderPane();
        baseLayout.setPrefSize(1350, 880);
        baseLayout.setMaxSize(1350, 880);
        baseLayout.setMinSize(1350, 880);
        baseLayout.setStyle("-fx-background-color: #0f172a;");
 
        // --- TOP BAR ---
        HBox topBox = new HBox(25);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10, 20, 10, 20));
        topBox.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 0 2 0;");
 
        Label targetLbl = new Label("🎯 Reach Cell 99 with ≥ 1000 Energy to win!");
        targetLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        targetLbl.setTextFill(Color.web("#10b981"));
 
        currentTurnLbl = new Label("Active Turn: ...");
        currentTurnLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        currentTurnLbl.setTextFill(Color.web("#f59e0b"));
        currentTurnLbl.setStyle("-fx-background-color: rgba(245,158,11,0.12); -fx-padding: 5 12; -fx-background-radius: 8;");
 
        // FIX: turn counter label
        turnCounterLbl = new Label("Turn: 1");
        turnCounterLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        turnCounterLbl.setTextFill(Color.web("#94a3b8"));
        turnCounterLbl.setStyle("-fx-background-color: rgba(148,163,184,0.1); -fx-padding: 5 12; -fx-background-radius: 8;");
 
        // FIX: last roll label always visible
        lastRollLbl = new Label("Last Roll: —");
        lastRollLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        lastRollLbl.setTextFill(Color.web("#a78bfa"));
        lastRollLbl.setStyle("-fx-background-color: rgba(167,139,250,0.1); -fx-padding: 5 12; -fx-background-radius: 8;");
 
        topBox.getChildren().addAll(targetLbl, currentTurnLbl, turnCounterLbl, lastRollLbl);
        baseLayout.setTop(topBox);
 
        // --- BOARD GRID ---
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(4);
        gridPane.setVgap(4);
        gridPane.setPadding(new Insets(10));
 
        Cell[][] engineBoard = game.getBoard().getBoardCells();
 
        for (int i = 0; i < 100; i++) {
            StackPane cellPane = new StackPane();
            cellPane.setPrefSize(70, 70);
 
            int row = i / 10;
            int col = i % 10;
            if (row % 2 == 1) col = 9 - col;
 
            int gridCol = col;
            int gridRow = 9 - row;
 
            Cell baseCell = engineBoard[row][col];
            Color cellBaseColor = Color.web("#334155");
 
            VBox innerContent = new VBox(1);
            innerContent.setAlignment(Pos.CENTER);
 
            Label graphicIcon = new Label();
            graphicIcon.setFont(Font.font(15));
            Label infoData = new Label();
            infoData.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            infoData.setTextFill(Color.WHITE);
 
            if (baseCell instanceof DoorCell) {
                DoorCell dc = (DoorCell) baseCell;
                graphicIcon.setText("🚪");
                infoData.setText("E: " + dc.getEnergy());
                cellBaseColor = dc.getRole() == Role.SCARER ? Color.web("#991b1b") : Color.web("#065f46");
            } else if (baseCell instanceof MonsterCell) {
                MonsterCell mc = (MonsterCell) baseCell;
                graphicIcon.setText("👾");
                String fullName = mc.getCellMonster().getName();
                infoData.setText(fullName.contains(" ") ? fullName.split(" ")[0] : fullName);
                infoData.setTextFill(Color.web("#bae6fd"));
                cellBaseColor = Color.web("#1e3a8a");
            } else if (baseCell instanceof CardCell) {
                graphicIcon.setText("🃏");
                infoData.setText("Mystery");
                cellBaseColor = Color.web("#9a3412");
            } else if (baseCell instanceof ConveyorBelt) {
                ConveyorBelt cb = (ConveyorBelt) baseCell;
                graphicIcon.setText("⏩");
                infoData.setText("+" + cb.getEffect());
                cellBaseColor = Color.web("#047857");
            } else if (baseCell instanceof ContaminationSock) {
                ContaminationSock cs = (ContaminationSock) baseCell;
                graphicIcon.setText("🧦");
                infoData.setText(cs.getEffect() + "");
                infoData.setTextFill(Color.web("#fca5a5"));
                cellBaseColor = Color.web("#b45309");
            } else {
                graphicIcon.setText("▫️");
                infoData.setText("Normal");
                infoData.setTextFill(Color.web("#94a3b8"));
            }
 
            innerContent.getChildren().addAll(graphicIcon, infoData);
 
            String hexCode = toHex(cellBaseColor);
            cellPane.setStyle("-fx-background-color: " + hexCode + "; -fx-background-radius: 6; -fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 6;");
 
            Label indexStamp = new Label(String.valueOf(i));
            indexStamp.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            indexStamp.setTextFill(Color.web("#cbd5e1"));
            StackPane.setAlignment(indexStamp, Pos.TOP_LEFT);
            StackPane.setMargin(indexStamp, new Insets(2, 0, 0, 4));
 
            // FIX: energy delta label for showing energy changes on board
            Label energyDeltaLbl = new Label("");
            energyDeltaLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            energyDeltaLbl.setVisible(false);
            StackPane.setAlignment(energyDeltaLbl, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(energyDeltaLbl, new Insets(0, 3, 2, 0));
 
            HBox markerLayer = new HBox(3);
            markerLayer.setAlignment(Pos.CENTER);
 
            cellPane.getChildren().addAll(innerContent, indexStamp, markerLayer, energyDeltaLbl);
            cellPanes[i] = cellPane;
            gridPane.add(cellPane, gridCol, gridRow);
        }
 
        // Highlight winning cell
        StackPane destinationTile = cellPanes[99];
        destinationTile.setStyle(destinationTile.getStyle() + "; -fx-border-color: #f59e0b; -fx-border-width: 3;");
 
        VBox gridWrapper = new VBox(gridPane);
        gridWrapper.setAlignment(Pos.CENTER);
        baseLayout.setCenter(gridWrapper);
 
        // --- SIDE PANELS ---
        playerDashboard = initSidePanel("YOU");
        opponentDashboard = initSidePanel("OPPONENT");
        baseLayout.setLeft(playerDashboard);
        baseLayout.setRight(opponentDashboard);
 
        // --- BOTTOM DOCK ---
        VBox bottomDock = new VBox(10);
        bottomDock.setPadding(new Insets(10, 20, 12, 20));
        bottomDock.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 2 0 0 0;");
 
        HBox commandButtonsRow = new HBox(25);
        commandButtonsRow.setAlignment(Pos.CENTER);
 
        powerupBtn = new Button("⚡ Use Powerup (costs 500 Energy) — activate BEFORE rolling");
        powerupBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        powerupBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 18; -fx-cursor: hand;");
        powerupBtn.setOnAction(e -> controller.handleUsePowerup(this));
 
        rollBtn = new Button("🎲 Roll Dice");
        rollBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        rollBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 22; -fx-cursor: hand;");
        rollBtn.setOnAction(e -> controller.handleRollDice(this));
 
        commandButtonsRow.getChildren().addAll(powerupBtn, rollBtn);
 
        HBox consoleDeckRow = new HBox(15);
        consoleDeckRow.setAlignment(Pos.CENTER);
 
        VBox consoleContainer = new VBox(4);
        Label consoleBanner = new Label("📋 Action Log");
        consoleBanner.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        consoleBanner.setTextFill(Color.web("#94a3b8"));
        actionLog = new ListView<>();
        actionLog.setPrefHeight(85);
        actionLog.setPrefWidth(680);
        actionLog.setStyle("-fx-control-inner-background: #0f172a; -fx-font-family: monospace; -fx-font-size: 12;");
        consoleContainer.getChildren().addAll(consoleBanner, actionLog);
 
        // Card display box
        cardDisplayBox = new VBox(4);
        cardDisplayBox.setAlignment(Pos.CENTER);
        cardDisplayBox.setPrefSize(270, 90);
        cardDisplayBox.setStyle("-fx-background-color: #9a3412; -fx-background-radius: 6; -fx-border-color: #f59e0b; -fx-border-width: 2; -fx-border-radius: 6; -fx-padding: 6;");
        cardDisplayBox.setVisible(false);
 
        Label cardTriggerBanner = new Label("🃏 CARD DRAWN!");
        cardTriggerBanner.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        cardTriggerBanner.setTextFill(Color.web("#fef08a"));
 
        cardNameLbl = new Label("");
        cardNameLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        cardNameLbl.setTextFill(Color.WHITE);
 
        cardEffectLbl = new Label("");
        cardEffectLbl.setFont(Font.font("Arial", 11));
        cardEffectLbl.setTextFill(Color.web("#ffedd5"));
        cardEffectLbl.setWrapText(true);
        cardEffectLbl.setAlignment(Pos.CENTER);
 
        cardDisplayBox.getChildren().addAll(cardTriggerBanner, cardNameLbl, cardEffectLbl);
        consoleDeckRow.getChildren().addAll(consoleContainer, cardDisplayBox);
        bottomDock.getChildren().addAll(commandButtonsRow, consoleDeckRow);
        baseLayout.setBottom(bottomDock);
 
        Group scalableGroup = new Group(baseLayout);
        root.getChildren().add(scalableGroup);
        StackPane.setAlignment(scalableGroup, Pos.CENTER);
 
        root.widthProperty().addListener((obs, oldV, newV) -> applyScaling());
        root.heightProperty().addListener((obs, oldV, newV) -> applyScaling());
    }
 
    private String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int)(c.getRed()*255), (int)(c.getGreen()*255), (int)(c.getBlue()*255));
    }
 
    private void applyScaling() {
        double scaleX = root.getWidth() / 1350.0;
        double scaleY = root.getHeight() / 880.0;
        double scale = Math.min(scaleX, scaleY);
        if (scale > 0) {
            baseLayout.setScaleX(scale);
            baseLayout.setScaleY(scale);
        }
    }
 
    private VBox initSidePanel(String headerLabel) {
        VBox sideContainer = new VBox(10);
        sideContainer.setPrefWidth(250);
        sideContainer.setPadding(new Insets(12));
        sideContainer.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 2 0 2;");
        Label mainTitle = new Label(headerLabel);
        mainTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        mainTitle.setTextFill(Color.web("#64748b"));
        sideContainer.getChildren().add(mainTitle);
        return sideContainer;
    }
 
    public void logMessage(String entry) {
        actionLog.getItems().add(entry);
        actionLog.scrollTo(actionLog.getItems().size() - 1);
    }
 
    public void showCardDrawn(Card card) {
        cardNameLbl.setText(card.getName());
        cardEffectLbl.setText(card.getDescription());
        cardDisplayBox.setVisible(true);
        FadeTransition animationFade = new FadeTransition(Duration.millis(350), cardDisplayBox);
        animationFade.setFromValue(0.1);
        animationFade.setToValue(1.0);
        animationFade.play();
    }
 
    // FIX: call this before playTurn() to snapshot energies for delta display
    public void snapshotEnergies() {
        playerEnergyBefore = game.getPlayer().getEnergy();
        opponentEnergyBefore = game.getOpponent().getEnergy();
    }
 
    // FIX: show last roll in dedicated label
    public void updateLastRoll(int roll) {
        lastRollLbl.setText("Last Roll: 🎲 " + roll);
    }
 
    // FIX: increment and display turn number
    public void incrementTurn() {
        turnCount++;
        turnCounterLbl.setText("Turn: " + turnCount);
    }
 
    // FIX: hide card display (called at start of each roll)
    public void hideCard() {
        cardDisplayBox.setVisible(false);
    }
 
    public void updateAll() {
        Monster pEntity = game.getPlayer();
        Monster oEntity = game.getOpponent();
        Monster activeEntity = game.getCurrent();
 
        // FIX: show current player name clearly with frozen indicator
        String turnText = activeEntity.getName() + " (" + activeEntity.getRole() + ")";
        if (activeEntity.isFrozen()) {
            turnText += "  ❄️ FROZEN — will skip turn!";
            currentTurnLbl.setStyle("-fx-background-color: rgba(147,197,253,0.2); -fx-padding: 5 12; -fx-background-radius: 8;");
            currentTurnLbl.setTextFill(Color.web("#93c5fd"));
        } else {
            currentTurnLbl.setStyle("-fx-background-color: rgba(245,158,11,0.12); -fx-padding: 5 12; -fx-background-radius: 8;");
            currentTurnLbl.setTextFill(Color.web("#f59e0b"));
        }
        currentTurnLbl.setText("Active: " + turnText);
 
        // Clear all token markers
        for (StackPane cell : cellPanes) {
            HBox tokenHost = (HBox) cell.getChildren().get(2);
            tokenHost.getChildren().clear();
        }
 
        // FIX: render tokens with confusion color change on board
        renderToken(pEntity, "P", pEntity.isConfused() ? Color.web("#f87171") : Color.web("#f59e0b"));
        renderToken(oEntity, "O", oEntity.isConfused() ? Color.web("#f87171") : Color.web("#ec4899"));
 
        // FIX: update door cell styles (non-activated / activated / exhausted are the same "activated" flag here,
        // but we distinguish visually: not activated = full color, activated = dimmed)
        Cell[][] logicBoard = game.getBoard().getBoardCells();
        for (int i = 0; i < 100; i++) {
            int row = i / 10;
            int col = i % 10;
            if (row % 2 == 1) col = 9 - col;
 
            Cell checkedCell = logicBoard[row][col];
            if (checkedCell instanceof DoorCell) {
                DoorCell dc = (DoorCell) checkedCell;
                if (dc.isActivated()) {
                    // FIX: activated/exhausted door — dimmed with "used" label
                    String baseColor = dc.getRole() == Role.SCARER ? "#4c0519" : "#022c22";
                    cellPanes[i].setStyle("-fx-background-color: " + baseColor + "; -fx-background-radius: 6; -fx-border-color: #475569; -fx-border-width: 1; -fx-border-radius: 6; -fx-opacity: 0.55;");
                } else {
                    String baseColor = dc.getRole() == Role.SCARER ? "#991b1b" : "#065f46";
                    cellPanes[i].setStyle("-fx-background-color: " + baseColor + "; -fx-background-radius: 6; -fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 6;");
                }
            }
 
            // FIX: refresh MonsterCell displayed name so it's never stale
            if (checkedCell instanceof MonsterCell) {
                MonsterCell mc = (MonsterCell) checkedCell;
                VBox inner = (VBox) cellPanes[i].getChildren().get(0);
                if (inner.getChildren().size() > 1) {
                    Label info = (Label) inner.getChildren().get(1);
                    String fullName = mc.getCellMonster().getName();
                    info.setText(fullName.contains(" ") ? fullName.split(" ")[0] : fullName);
                }
            }
        }
 
        // FIX: show energy delta on the cell where each monster currently stands
        showEnergyDelta(pEntity, playerEnergyBefore);
        showEnergyDelta(oEntity, opponentEnergyBefore);
 
        refreshPanelStats(playerDashboard, pEntity, pEntity == activeEntity, true);
        refreshPanelStats(opponentDashboard, oEntity, oEntity == activeEntity, false);
 
        // Disable powerup if not enough energy OR if it's not their turn (prevent mis-clicks)
        powerupBtn.setDisable(activeEntity.getEnergy() < 500);
    }
 
    // FIX: flash energy delta label on board cell
    private void showEnergyDelta(Monster monster, int energyBefore) {
        int pos = monster.getPosition();
        if (pos < 0 || pos >= 100) return;
        int delta = monster.getEnergy() - energyBefore;
        if (delta == 0) return;
 
        Label deltaLbl = (Label) cellPanes[pos].getChildren().get(3);
        String sign = delta > 0 ? "+" : "";
        deltaLbl.setText(sign + delta);
        deltaLbl.setTextFill(delta > 0 ? Color.web("#4ade80") : Color.web("#f87171"));
        deltaLbl.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        deltaLbl.setVisible(true);
 
        // Fade out after 2 seconds
        FadeTransition fade = new FadeTransition(Duration.millis(2000), deltaLbl);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> deltaLbl.setVisible(false));
        fade.play();
    }
 
    // FIX: show shield block on board cell
    public void showShieldBlock(Monster monster) {
        int pos = monster.getPosition();
        if (pos < 0 || pos >= 100) return;
        Label deltaLbl = (Label) cellPanes[pos].getChildren().get(3);
        deltaLbl.setText("🛡️ BLOCKED");
        deltaLbl.setTextFill(Color.web("#60a5fa"));
        deltaLbl.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        deltaLbl.setVisible(true);
 
        FadeTransition fade = new FadeTransition(Duration.millis(2500), deltaLbl);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> deltaLbl.setVisible(false));
        fade.play();
    }
 
    private void renderToken(Monster targetMonster, String letterCode, Color hueColor) {
        int targetCoord = targetMonster.getPosition();
        if (targetCoord >= 0 && targetCoord < 100) {
            StackPane basePane = cellPanes[targetCoord];
            HBox targetBox = (HBox) basePane.getChildren().get(2);
 
            StackPane avatarWrapper = new StackPane();
            Circle circleBase = new Circle(11, hueColor);
            circleBase.setEffect(new DropShadow(3, Color.BLACK));
 
            // FIX: if confused, add swirl overlay on token
            Label initialLabel = new Label(targetMonster.isConfused() ? "🌀" : letterCode);
            initialLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            initialLabel.setTextFill(Color.WHITE);
 
            avatarWrapper.getChildren().addAll(circleBase, initialLabel);
            targetBox.getChildren().add(avatarWrapper);
        }
    }
 
    private void refreshPanelStats(VBox panelBox, Monster dataMonster, boolean isActive, boolean isPlayer) {
        Label persistentHeader = (Label) panelBox.getChildren().get(0);
        panelBox.getChildren().clear();
        panelBox.getChildren().add(persistentHeader);
 
        // FIX: header label now clearly says who is the current active player
        persistentHeader.setText(isPlayer ? "YOU" : "OPPONENT");
        if (isActive) {
            panelBox.setStyle("-fx-background-color: #1e293b; -fx-border-color: #10b981; -fx-border-width: 2; -fx-border-radius: 6;");
            persistentHeader.setTextFill(Color.web("#10b981"));
        } else {
            panelBox.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 2 0 2;");
            persistentHeader.setTextFill(Color.web("#64748b"));
        }
 
        Label nameStamp = new Label(dataMonster.getName());
        nameStamp.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        nameStamp.setTextFill(Color.WHITE);
        nameStamp.setWrapText(true);
 
        Label origRoleInfo = new Label("Original Role: " + dataMonster.getOriginalRole());
        origRoleInfo.setFont(Font.font("Arial", 12));
        origRoleInfo.setTextFill(Color.web("#94a3b8"));
 
        // FIX: current role clearly indicates confusion
        Label currentRoleInfo = new Label("Current Role: " + dataMonster.getRole());
        currentRoleInfo.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        if (dataMonster.isConfused()) {
            currentRoleInfo.setTextFill(Color.web("#ef4444"));
            currentRoleInfo.setText("Current Role: " + dataMonster.getRole() + "  🌀 CONFUSED");
            currentRoleInfo.setStyle("-fx-background-color: rgba(239,68,68,0.1); -fx-padding: 2 5; -fx-background-radius: 4;");
        } else {
            currentRoleInfo.setTextFill(Color.web("#10b981"));
            currentRoleInfo.setStyle("");
        }
 
        String classStr = "Standard";
        if (dataMonster instanceof Dasher) classStr = "⚡ Dasher";
        else if (dataMonster instanceof Dynamo) classStr = "💥 Dynamo";
        else if (dataMonster instanceof MultiTasker) classStr = "🎯 MultiTasker";
        else if (dataMonster instanceof Schemer) classStr = "🕶️ Schemer";
 
        Label classLabel = new Label("Type: " + classStr);
        classLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        classLabel.setTextFill(Color.web("#cbd5e1"));
 
        VBox gaugeBox = new VBox(3);
        Label gaugeLabel = new Label("Energy: " + dataMonster.getEnergy());
        gaugeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gaugeLabel.setTextFill(Color.web("#f59e0b"));
 
        ProgressBar fuelGauge = new ProgressBar(Math.min(1.0, (double) dataMonster.getEnergy() / 1000.0));
        fuelGauge.setPrefWidth(210);
        fuelGauge.setStyle("-fx-accent: #f59e0b;");
        gaugeBox.getChildren().addAll(gaugeLabel, fuelGauge);
 
        Label targetPosLabel = new Label("📍 Position: " + dataMonster.getPosition());
        targetPosLabel.setFont(Font.font("Arial", 12));
        targetPosLabel.setTextFill(Color.WHITE);
 
        // --- Status Effects Box ---
        VBox buffBox = new VBox(4);
        buffBox.setStyle("-fx-background-color: #0f172a; -fx-padding: 8; -fx-background-radius: 5;");
 
        Label buffBanner = new Label("Active Status Effects:");
        buffBanner.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        buffBanner.setTextFill(Color.web("#94a3b8"));
        buffBox.getChildren().add(buffBanner);
 
        boolean buffFound = false;
 
        if (dataMonster.isShielded()) {
            Label shld = new Label("🛡️ Shield Active — blocks next energy loss");
            shld.setTextFill(Color.web("#60a5fa"));
            shld.setFont(Font.font("Arial", 11));
            buffBox.getChildren().add(shld);
            buffFound = true;
        }
        // FIX: freeze shown prominently with clear skip-turn warning
        if (dataMonster.isFrozen()) {
            Label frz = new Label("❄️ FROZEN — next turn will be SKIPPED");
            frz.setTextFill(Color.web("#93c5fd"));
            frz.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            frz.setStyle("-fx-background-color: rgba(147,197,253,0.15); -fx-padding: 2 5; -fx-background-radius: 4;");
            buffBox.getChildren().add(frz);
            buffFound = true;
        }
        if (dataMonster.isConfused()) {
            Label cfs = new Label("🌀 Confused — " + dataMonster.getConfusionTurns() + " turns remaining");
            cfs.setTextFill(Color.web("#f87171"));
            cfs.setFont(Font.font("Arial", 11));
            buffBox.getChildren().add(cfs);
            buffFound = true;
        }
        if (dataMonster instanceof Dasher && ((Dasher)dataMonster).getMomentumTurns() > 0) {
            Label mmt = new Label("⚡ Momentum Rush — " + ((Dasher)dataMonster).getMomentumTurns() + " turns (3x speed)");
            mmt.setTextFill(Color.web("#fde047"));
            mmt.setFont(Font.font("Arial", 11));
            buffBox.getChildren().add(mmt);
            buffFound = true;
        }
        if (dataMonster instanceof MultiTasker && ((MultiTasker)dataMonster).getNormalSpeedTurns() > 0) {
            Label fcs = new Label("🎯 Focus Mode — " + ((MultiTasker)dataMonster).getNormalSpeedTurns() + " turns (normal speed)");
            fcs.setTextFill(Color.web("#a7f3d0"));
            fcs.setFont(Font.font("Arial", 11));
            buffBox.getChildren().add(fcs);
            buffFound = true;
        }
 
        if (!buffFound) {
            Label nullMod = new Label("None");
            nullMod.setTextFill(Color.web("#64748b"));
            nullMod.setFont(Font.font("Arial", 11));
            buffBox.getChildren().add(nullMod);
        }
 
        panelBox.getChildren().addAll(nameStamp, origRoleInfo, currentRoleInfo, classLabel, gaugeBox, targetPosLabel, buffBox);
    }
}
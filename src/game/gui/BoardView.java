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
    private StackPane root; // Outer scalable root view
    private BorderPane baseLayout; // Fixed 1350x880 operational base layer
    private GameController controller;
    private Game game;

    // Dynamically tracking UI references
    private GridPane gridPane;
    private VBox playerDashboard;
    private VBox opponentDashboard;
    private Label currentTurnLbl;
    private Button powerupBtn;
    private Button rollBtn;
    private ListView<String> actionLog;
    private VBox cardDisplayBox;
    private Label cardNameLbl;
    private Label cardEffectLbl;

    private StackPane[] cellPanes = new StackPane[100];

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

        // 1. Establish the base internal view boundary perfectly
        baseLayout = new BorderPane();
        baseLayout.setPrefSize(1350, 880);
        baseLayout.setMaxSize(1350, 880);
        baseLayout.setMinSize(1350, 880);
        baseLayout.setStyle("-fx-background-color: #0f172a;");

        // Top Target Banner
        HBox topBox = new HBox(35);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(12, 20, 12, 20));
        topBox.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 0 2 0;");

        Label targetLbl = new Label("🎯 Mission Indicator: Touch down on Cell 99 maintaining ≥ 1000 Total Energy!");
        targetLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        targetLbl.setTextFill(Color.web("#10b981"));

        currentTurnLbl = new Label("Active Turn: Execution Queue...");
        currentTurnLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 17));
        currentTurnLbl.setTextFill(Color.web("#f59e0b"));
        currentTurnLbl.setStyle("-fx-background-color: rgba(245, 158, 11, 0.12); -fx-padding: 6 15; -fx-background-radius: 8;");

        topBox.getChildren().addAll(targetLbl, currentTurnLbl);
        baseLayout.setTop(topBox);

        // Center Grid Layout Setup - Uses clean baseline proportional square measurements
        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(12));

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
                infoData.setText("Corridor");
                infoData.setTextFill(Color.web("#94a3b8"));
            }

            innerContent.getChildren().addAll(graphicIcon, infoData);

            String hexCode = String.format("#%02X%02X%02X",
                (int)(cellBaseColor.getRed()*255), (int)(cellBaseColor.getGreen()*255), (int)(cellBaseColor.getBlue()*255));
            cellPane.setStyle("-fx-background-color: " + hexCode + "; -fx-background-radius: 6; -fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 6;");

            Label indexStamp = new Label(String.valueOf(i));
            indexStamp.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            indexStamp.setTextFill(Color.web("#cbd5e1"));
            StackPane.setAlignment(indexStamp, Pos.TOP_LEFT);
            StackPane.setMargin(indexStamp, new Insets(2, 0, 0, 4));

            HBox markerLayer = new HBox(3);
            markerLayer.setAlignment(Pos.CENTER);

            cellPane.getChildren().addAll(innerContent, indexStamp, markerLayer);
            cellPanes[i] = cellPane;
            gridPane.add(cellPane, gridCol, gridRow);
        }

        StackPane destinationTile = cellPanes[99];
        destinationTile.setStyle(destinationTile.getStyle() + "; -fx-border-color: #f59e0b; -fx-border-width: 3;");

        VBox gridWrapper = new VBox(gridPane);
        gridWrapper.setAlignment(Pos.CENTER);
        baseLayout.setCenter(gridWrapper);

        // Side Panels Setup
        playerDashboard = initSidePanel("PLAYER CONTAINER (You)");
        opponentDashboard = initSidePanel("OPPONENT RESIDENCE");
        baseLayout.setLeft(playerDashboard);
        baseLayout.setRight(opponentDashboard);

        // Bottom Operations Dock Area
        VBox bottomDock = new VBox(12);
        bottomDock.setPadding(new Insets(12, 20, 15, 20));
        bottomDock.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 2 0 0 0;");

        HBox commandButtonsRow = new HBox(25);
        commandButtonsRow.setAlignment(Pos.CENTER);

        powerupBtn = new Button("⚡ Fire Ability Powerup (500 E)");
        powerupBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        powerupBtn.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 18; -fx-cursor: hand;");
        powerupBtn.setOnAction(e -> controller.handleUsePowerup(this));

        rollBtn = new Button("🎲 Execute Dice Roll Step");
        rollBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        rollBtn.setStyle("-fx-background-color: #059669; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 22; -fx-cursor: hand;");
        rollBtn.setOnAction(e -> controller.handleRollDice(this));

        commandButtonsRow.getChildren().addAll(powerupBtn, rollBtn);

        HBox consoleDeckRow = new HBox(15);
        consoleDeckRow.setAlignment(Pos.CENTER);

        VBox consoleContainer = new VBox(4);
        Label consoleBanner = new Label("📋 Floor Logging Terminal System Console");
        consoleBanner.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        consoleBanner.setTextFill(Color.web("#94a3b8"));
        
        actionLog = new ListView<>();
        actionLog.setPrefHeight(90);
        actionLog.setPrefWidth(680);
        actionLog.setStyle("-fx-control-inner-background: #0f172a; -fx-font-family: monospace; -fx-font-size: 12;");
        consoleContainer.getChildren().addAll(consoleBanner, actionLog);

        cardDisplayBox = new VBox(4);
        cardDisplayBox.setAlignment(Pos.CENTER);
        cardDisplayBox.setPrefSize(270, 90);
        cardDisplayBox.setStyle("-fx-background-color: #9a3412; -fx-background-radius: 6; -fx-border-color: #f59e0b; -fx-border-width: 2; -fx-border-radius: 6; -fx-padding: 6;");
        cardDisplayBox.setVisible(false);

        Label cardTriggerBanner = new Label("🃏 MYSTERY CARD REVEALED!");
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

        // 2. Wrap operational base layout squarely inside a transform layer Group
        Group scalableGroup = new Group(baseLayout);
        root.getChildren().add(scalableGroup);
        StackPane.setAlignment(scalableGroup, Pos.CENTER);

        // 3. Anchor structural dimensions natively against real-time listeners
        root.widthProperty().addListener((obs, oldV, newV) -> applyScaling());
        root.heightProperty().addListener((obs, oldV, newV) -> applyScaling());
    }

    private void applyScaling() {
        double scaleX = root.getWidth() / 1350.0;
        double scaleY = root.getHeight() / 880.0;
        // The scale factor is guaranteed to preserve uniform dimension outputs cleanly
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

    public void updateAll() {
        Monster pEntity = game.getPlayer();
        Monster oEntity = game.getOpponent();
        Monster activeEntity = game.getCurrent();

        currentTurnLbl.setText("Active Turn: " + activeEntity.getName() + " (" + activeEntity.getRole() + ")");

        for (StackPane cell : cellPanes) {
            HBox tokenHost = (HBox) cell.getChildren().get(2);
            tokenHost.getChildren().clear();
        }

        renderToken(pEntity, "P", Color.web("#f59e0b")); 
        renderToken(oEntity, "O", Color.web("#ec4899")); 

        Cell[][] logicBoard = game.getBoard().getBoardCells();
        for (int i = 0; i < 100; i++) {
            int row = i / 10;
            int col = i % 10;
            if (row % 2 == 1) col = 9 - col;
            
            Cell checkedCell = logicBoard[row][col];
            if (checkedCell instanceof DoorCell) {
                DoorCell dc = (DoorCell) checkedCell;
                if (dc.isActivated()) {
                    cellPanes[i].setStyle("-fx-background-color: #0f172a; -fx-background-radius: 6; -fx-opacity: 0.45;");
                }
            }
        }

        refreshPanelStats(playerDashboard, pEntity, pEntity == activeEntity);
        refreshPanelStats(opponentDashboard, oEntity, oEntity == activeEntity);

        powerupBtn.setDisable(activeEntity.getEnergy() < 500);
    }

    private void renderToken(Monster targetMonster, String letterCode, Color hueColor) {
        int targetCoord = targetMonster.getPosition();
        if (targetCoord >= 0 && targetCoord < 100) {
            StackPane basePane = cellPanes[targetCoord];
            HBox targetBox = (HBox) basePane.getChildren().get(2);
            
            StackPane avatarWrapper = new StackPane();
            Circle circleBase = new Circle(11, hueColor);
            circleBase.setEffect(new DropShadow(3, Color.BLACK));
            
            Label initialLabel = new Label(letterCode);
            initialLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            initialLabel.setTextFill(Color.WHITE);

            avatarWrapper.getChildren().addAll(circleBase, initialLabel);
            targetBox.getChildren().add(avatarWrapper);
        }
    }

    private void refreshPanelStats(VBox panelBox, Monster dataMonster, boolean maintainsActiveTurn) {
        Label persistentHeader = (Label) panelBox.getChildren().get(0);
        panelBox.getChildren().clear();
        panelBox.getChildren().add(persistentHeader);

        if (maintainsActiveTurn) {
            panelBox.setStyle("-fx-background-color: #1e293b; -fx-border-color: #10b981; -fx-border-width: 2; -fx-border-radius: 6;");
        } else {
            panelBox.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 2 0 2;");
        }

        Label nameStamp = new Label(dataMonster.getName());
        nameStamp.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        nameStamp.setTextFill(Color.WHITE);
        nameStamp.setWrapText(true);

        Label origRoleInfo = new Label("Base Allegiance: " + dataMonster.getOriginalRole());
        origRoleInfo.setFont(Font.font("Arial", 12));
        origRoleInfo.setTextFill(Color.web("#94a3b8"));

        Label currentRoleInfo = new Label("Active Role: " + dataMonster.getRole());
        currentRoleInfo.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        if (dataMonster.getRole() != dataMonster.getOriginalRole()) {
            currentRoleInfo.setTextFill(Color.web("#ef4444")); 
            currentRoleInfo.setText(currentRoleInfo.getText() + " 🌀 (CONFUSED)");
        } else {
            currentRoleInfo.setTextFill(Color.web("#10b981"));
        }

        String classStr = "Standard Class";
        if (dataMonster instanceof Dasher) classStr = "⚡ Dasher Speedster";
        else if (dataMonster instanceof Dynamo) classStr = "💥 Dynamo Heavy";
        else if (dataMonster instanceof MultiTasker) classStr = "🎯 MultiTasker";
        else if (dataMonster instanceof Schemer) classStr = "🕶️ Schemer Agent";

        Label classLabel = new Label("Classification: " + classStr);
        classLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 12));
        classLabel.setTextFill(Color.web("#cbd5e1"));

        VBox gaugeBox = new VBox(3);
        Label gaugeLabel = new Label("Energy Reserves: " + dataMonster.getEnergy());
        gaugeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gaugeLabel.setTextFill(Color.web("#f59e0b"));

        ProgressBar fuelGauge = new ProgressBar((double) dataMonster.getEnergy() / 1000.0);
        fuelGauge.setPrefWidth(210);
        fuelGauge.setStyle("-fx-accent: #f59e0b;");
        gaugeBox.getChildren().addAll(gaugeLabel, fuelGauge);

        Label targetPosLabel = new Label("📍 Floor Index Coord: " + dataMonster.getPosition());
        targetPosLabel.setFont(Font.font("Arial", 12));
        targetPosLabel.setTextFill(Color.WHITE);

        VBox buffBox = new VBox(4);
        buffBox.setStyle("-fx-background-color: #0f172a; -fx-padding: 8; -fx-background-radius: 5;");
        
        Label buffBanner = new Label("Active System Modifiers:");
        buffBanner.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        buffBanner.setTextFill(Color.web("#94a3b8"));
        buffBox.getChildren().add(buffBanner);

        boolean buffFound = false;
        if (dataMonster.isShielded()) {
            Label shld = new Label("🛡️ Shield Barrier Engaged");
            shld.setTextFill(Color.web("#60a5fa"));
            buffBox.getChildren().add(shld);
            buffFound = true;
        }
        if (dataMonster.isFrozen()) {
            Label frz = new Label("❄️ Stunned (Skips next turn)");
            frz.setTextFill(Color.web("#93c5fd"));
            buffBox.getChildren().add(frz);
            buffFound = true;
        }
        if (dataMonster.isConfused()) {
            Label cfs = new Label("🌀 Confusion Active (" + dataMonster.getConfusionTurns() + " turns)");
            cfs.setTextFill(Color.web("#f87171"));
            buffBox.getChildren().add(cfs);
            buffFound = true;
        }
        if (dataMonster instanceof Dasher && ((Dasher)dataMonster).getMomentumTurns() > 0) {
            Label mmt = new Label("⚡ Momentum Rush (" + ((Dasher)dataMonster).getMomentumTurns() + " turns)");
            mmt.setTextFill(Color.web("#fde047"));
            buffBox.getChildren().add(mmt);
            buffFound = true;
        }
        if (dataMonster instanceof MultiTasker && ((MultiTasker)dataMonster).getNormalSpeedTurns() > 0) {
            Label fcs = new Label("🎯 Focus Mode Active (" + ((MultiTasker)dataMonster).getNormalSpeedTurns() + " turns)");
            fcs.setTextFill(Color.web("#a7f3d0"));
            buffBox.getChildren().add(fcs);
            buffFound = true;
        }

        if (!buffFound) {
            Label nullMod = new Label("None Active");
            nullMod.setTextFill(Color.web("#64748b"));
            buffBox.getChildren().add(nullMod);
        }

        panelBox.getChildren().addAll(nameStamp, origRoleInfo, currentRoleInfo, classLabel, gaugeBox, targetPosLabel, buffBox);
    }
}
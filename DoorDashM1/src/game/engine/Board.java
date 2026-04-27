package game.engine;

import java.util.ArrayList;
import java.util.Collections;

import game.engine.cards.Card;
import game.engine.cells.*;
import game.engine.exceptions.InvalidMoveException;
import game.engine.monsters.Monster;

public class Board {
	private Cell[][] boardCells;
	private static ArrayList<Monster> stationedMonsters; 
	private static ArrayList<Card> originalCards;
	public static ArrayList<Card> cards;
	
	public Board(ArrayList<Card> readCards) {
		this.boardCells = new Cell[Constants.BOARD_ROWS][Constants.BOARD_COLS];
		stationedMonsters = new ArrayList<Monster>();
		originalCards = readCards;
		cards = new ArrayList<Card>();
		setCardsByRarity();
		reloadCards();
	}
	
	public Cell[][] getBoardCells() {
		return boardCells;
	}
	
	public static ArrayList<Monster> getStationedMonsters() {
		return stationedMonsters;
	}
	
	public static void setStationedMonsters(ArrayList<Monster> stationedMonsters) {
		Board.stationedMonsters = stationedMonsters;
	}

	public static ArrayList<Card> getOriginalCards() {
		return originalCards;
	}
	
	public static ArrayList<Card> getCards() {
		return cards;
	}
	
	public static void setCards(ArrayList<Card> cards) {
		Board.cards = cards;
	}

	public static Card drawCard() {
		if (cards.isEmpty()) {
			reloadCards();
		}
		return cards.remove(0);
	}
	private int[] indexToRowCol(int index) {
		int row = index / 10;
		int col = index % 10;
		if (row % 2 != 0) {
			col = 9 - col;
		}
		return new int[] { row, col };
	}
	
	private Cell getCell(int index) {
		int[] pos = indexToRowCol(index);
		return boardCells[pos[0]][pos[1]];
	}

	private void setCell(int index, Cell cell) {
		int[] pos = indexToRowCol(index);
		boardCells[pos[0]][pos[1]] = cell;
	}
	public void initializeBoard(ArrayList<Cell> specialCells) {
	  
	    ArrayList<DoorCell> doors = new ArrayList<>();
	    ArrayList<ConveyorBelt> belts = new ArrayList<>();
	    ArrayList<ContaminationSock> socks = new ArrayList<>();

	    for (Cell cell : specialCells) {
	        if (cell instanceof DoorCell) {
	            doors.add((DoorCell) cell);
	        } else if (cell instanceof ConveyorBelt) {
	            belts.add((ConveyorBelt) cell);
	        } else if (cell instanceof ContaminationSock) {
	            socks.add((ContaminationSock) cell);
	        }
	    }
	    int doorIndex = 0;
	    int beltIndex = 0;
	    int sockIndex = 0;

	    for (int i = 0; i < Constants.BOARD_SIZE; i++) {
	        if (i % 2 == 0) {
	            setCell(i, new Cell("Rest Cell"));
	        } 
	        else {
	        	
	            if (doorIndex < doors.size()) {
	                setCell(i, doors.get(doorIndex++));
	            }
	        }
	    }

	    for (int idx : Constants.CARD_CELL_INDICES) {
	        setCell(idx, new CardCell("Card Cell"));
	    }

	    for (int idx : Constants.CONVEYOR_CELL_INDICES) {
	        if (beltIndex < belts.size()) {
	            setCell(idx, belts.get(beltIndex++));
	        }
	    }

	    for (int idx : Constants.SOCK_CELL_INDICES) {
	        if (sockIndex < socks.size()) {
	            setCell(idx, socks.get(sockIndex++));
	        }
	    }

	    int monsterIndex = 0;
	    for (int idx : Constants.MONSTER_CELL_INDICES) {
	       
	        if (monsterIndex < stationedMonsters.size()) {
	            Monster stationedMonster = stationedMonsters.get(monsterIndex++);
	            
	            stationedMonster.setPosition(idx); 
	            setCell(idx, new MonsterCell("Monster Cell", stationedMonster));
	        }
	    }
	}
	private void setCardsByRarity() {
		ArrayList<Card> expandedList = new ArrayList<Card>();
		for (Card card : originalCards) {
			for (int i = 0; i < card.getRarity(); i++) {
				expandedList.add(card);
			}
		}

		originalCards = expandedList;
	}
	
	public static void reloadCards() {
		cards = new ArrayList<Card>(originalCards);
		Collections.shuffle(cards);
	}
	public void moveMonster(Monster currentMonster, int roll, Monster opponentMonster) throws InvalidMoveException {
		
		int oldPosition = currentMonster.getPosition();
		int currentOldConfusion = currentMonster.getConfusionTurns();
		int opponentOldConfusion = opponentMonster.getConfusionTurns();
		
		int newPosition = currentMonster.getPosition() + roll;
		currentMonster.setPosition(newPosition);
		Cell landedCell = getCell(currentMonster.getPosition());
		if (landedCell != null) {
			landedCell.onLand(currentMonster); 
		}

		if (currentMonster.getPosition() == opponentMonster.getPosition()) {
			currentMonster.setPosition(oldPosition); 
			throw new InvalidMoveException("Invalid Move: Collision with opponent monster!");
		}

		if (currentMonster.getConfusionTurns() > 0 && currentOldConfusion > 0) {
			currentMonster.setConfusionTurns(currentMonster.getConfusionTurns() - 1);
		}
		
		if (opponentMonster.getConfusionTurns() > 0 && opponentOldConfusion > 0) {
			opponentMonster.setConfusionTurns(opponentMonster.getConfusionTurns() - 1);
		}

		updateMonsterPositions(currentMonster, opponentMonster);
	}

	private void updateMonsterPositions(Monster player, Monster opponent) {
		for (int i = 0; i < Constants.BOARD_SIZE; i++) {
			Cell cell = getCell(i);
			if (cell != null) {
				cell.setMonster(null); 
			}
		}

		Cell playerCell = getCell(player.getPosition());
		if (playerCell != null) {
			playerCell.setMonster(player);
		}

		Cell opponentCell = getCell(opponent.getPosition());
		if (opponentCell != null) {
			opponentCell.setMonster(opponent);
		}
	
	
}
	

package game.engine;

import java.util.ArrayList;
import java.util.Collections;

import game.engine.cards.Card;
import game.engine.cells.CardCell;
import game.engine.cells.Cell;
import game.engine.cells.ContaminationSock;
import game.engine.cells.ConveyorBelt;
import game.engine.cells.MonsterCell;
import game.engine.exceptions.InvalidMoveException;
import game.engine.monsters.Monster;

public class Board {

	private Cell[][] boardCells;
	private static ArrayList<Monster> stationedMonsters;
	private static ArrayList<Card> originalCards;
	public static ArrayList<Card> cards;

	public Board(ArrayList<Card> readCards) {
		this.boardCells = new Cell[Constants.BOARD_ROWS][Constants.BOARD_COLS];
		stationedMonsters = new ArrayList<>();
		originalCards = readCards;
		cards = new ArrayList<>();

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

	public void initializeBoard(ArrayList<Cell> specialCells) {

		int doorIndex = 0;
		for (int i = 0; i < 100; i++) {
			if (i % 2 == 0)
				setCell(i, new Cell("Normal Cell"));
			else
				setCell(i, specialCells.get(doorIndex++));
		}

		int beltIndex = 50;
		for (int i = 0; i < Constants.CONVEYOR_CELL_INDICES.length; i++) {
			setCell(Constants.CONVEYOR_CELL_INDICES[i], (ConveyorBelt) specialCells.get(beltIndex));
			beltIndex += 2;
		}

		int sockIndex = 51;
		for (int i = 0; i < Constants.SOCK_CELL_INDICES.length; i++) {
			setCell(Constants.SOCK_CELL_INDICES[i], (ContaminationSock) specialCells.get(sockIndex));
			sockIndex += 2;
		}

		for (int i = 0; i < Constants.CARD_CELL_INDICES.length; i++) {
			setCell(Constants.CARD_CELL_INDICES[i], new CardCell("Card Cell"));
		}

		ArrayList<Monster> stationed = Board.getStationedMonsters();
		for (int i = 0; i < Constants.MONSTER_CELL_INDICES.length && i<stationed.size(); i++) {
			int index = Constants.MONSTER_CELL_INDICES[i];
			Monster m = stationed.get(i);
			m.setPosition(index);
			setCell(index, new MonsterCell(m.getName(), m));
		}
	}
	
	public static void reloadCards() {
		cards = new ArrayList<>(originalCards);
		Collections.shuffle(cards);
	}

	public static Card drawCard() {
		if (cards.isEmpty())
			reloadCards();

		return cards.remove(0);
	}

	private int[] indexToRowCol(int index) {
		int row = index / Constants.BOARD_COLS;
		int col;

		if (row % 2 == 0)
			col = index % Constants.BOARD_COLS;
		else
			col = Constants.BOARD_COLS - 1 - (index % Constants.BOARD_COLS);

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

	private void setCardsByRarity() {
		ArrayList<Card> expanded = new ArrayList<>();

		for (Card c : originalCards) {
			for (int i = 0; i < c.getRarity(); i++) {
				expanded.add(c);
			}
		}

		originalCards = expanded;
	}

	public void moveMonster(Monster currentMonster, int roll, Monster opponentMonster)
			throws InvalidMoveException {

		boolean confused = false;
		if(currentMonster.isConfused())
			confused = true ;
		int oldPosition = currentMonster.getPosition();
		currentMonster.move(roll); 
		int newPosition = currentMonster.getPosition();

		if (newPosition == opponentMonster.getPosition()) {
			currentMonster.setPosition(oldPosition); // revert
			throw new InvalidMoveException();
		}
		else {
			Cell landingCell = getCell(newPosition);               
			landingCell.onLand(currentMonster, opponentMonster);           	
			updateMonsterPositions(currentMonster, opponentMonster);
			if(confused){
				currentMonster.decrementConfusion();
				opponentMonster.decrementConfusion();
			}
		}
	}

	private void updateMonsterPositions(Monster player, Monster opponent) {

		int total = Constants.BOARD_ROWS * Constants.BOARD_COLS;

		for (int i = 0; i < total; i++) {
			getCell(i).setMonster(null);
		}

		getCell(player.getPosition()).setMonster(player);
		getCell(opponent.getPosition()).setMonster(opponent);
	}
}

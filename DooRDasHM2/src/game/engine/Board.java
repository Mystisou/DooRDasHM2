package game.engine;

import java.util.ArrayList;

import game.engine.cards.Card;
import game.engine.cells.Cell;
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
		
	}
	private int[] indexToRowCol(int index) {
		 return new int[2];
	}
	public void moveMonster(Monster currentMonster, int roll, Monster opponentMonster) throws InvalidMoveException{
		int landingPosition = currentMonster.getPosition() + roll ;
		if(landingPosition == opponentMonster.getPosition())
        	throw new InvalidMoveException();
		else {
		    int[] landingCoord = indexToRowCol(landingPosition);
		    Cell landingCell = boardCells[landingCoord[0]][landingCoord[1]]; 
		    currentMonster.move(roll);
		    landingCell.onLand(currentMonster,opponentMonster);
		    updateMonsterPositions(currentMonster,opponentMonster);
	    }
   }
	private void updateMonsterPositions(Monster player, Monster opponent) {
		
	}
	
}
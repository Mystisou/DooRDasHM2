package game.engine;

import java.util.*;

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

        int total = Constants.BOARD_ROWS * Constants.BOARD_COLS;

        for (int i = 0; i < total; i++) {
            setCell(i, new Cell("Cell"));
        }

        for (int i = 0; i < specialCells.size(); i++) {
            setCell(i, specialCells.get(i));
        }

        
        for (Monster m : stationedMonsters) {
            getCell(m.getPosition()).setMonster(m);
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

        int landingPosition = currentMonster.getPosition() + roll;

        if (landingPosition == opponentMonster.getPosition())
            throw new InvalidMoveException();
        else {
        	boolean confused = false;
            int[] landingCoord = indexToRowCol(landingPosition);
            Cell landingCell = boardCells[landingCoord[0]][landingCoord[1]];
            if(currentMonster.isConfused())
            	confused = true;
            currentMonster.move(roll);
            landingCell.onLand(currentMonster, opponentMonster);
            if(confused)
            	currentMonster.decrementConfusion();
            updateMonsterPositions(currentMonster, opponentMonster);
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
package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import game.engine.dataloader.DataLoader;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.Monster;

public class Game {
	private Board board;
	private ArrayList<Monster> allMonsters; 
	private Monster player;
	private Monster opponent;
	private Monster current;
	
	public Game(Role playerRole) throws IOException {
		this.board = new Board(DataLoader.readCards());		
		this.allMonsters = DataLoader.readMonsters();		
		this.player = selectRandomMonsterByRole(playerRole);
		this.opponent = selectRandomMonsterByRole(playerRole == Role.SCARER ? Role.LAUGHER : Role.SCARER);
		this.current = player;
		allMonsters.remove(player);
		allMonsters.remove(opponent);
		Board.setStationedMonsters(allMonsters);
		board.initializeBoard(DataLoader.readCells());
	}
	public static void main(String[] args) {
		try {
	        Game myGame = new Game(Role.LAUGHER);
	    } catch (IOException e) {
	        System.out.println("Error: Could not load the game files. Check your CSVs!");
	        e.printStackTrace();
	    }
	}
	public Board getBoard() {
		return board;
	}
	
	public ArrayList<Monster> getAllMonsters() {
		return allMonsters; 
	}
	
	public Monster getPlayer() {
		return player;
	}
	
	public Monster getOpponent() {
		return opponent;
	}
	
	public Monster getCurrent() {
		return current;
	}
	
	public void setCurrent(Monster current) {
		this.current = current;
	}
	
	private Monster selectRandomMonsterByRole(Role role) {
		Collections.shuffle(allMonsters);
	    return allMonsters.stream()
	    		.filter(m -> m.getRole() == role)
	    		.findFirst()
	    		.orElse(null);
	}
	
	private Monster getCurrentOpponent() {
		if(current==opponent)
			return player;
		return opponent;
	}
	
	private int rollDice() {
		return (int)(Math.random()*6)+1	;
	}

	public void usePowerup() throws OutOfEnergyException {
		if (current.getEnergy() >= Constants.POWERUP_COST) {
			current.alterEnergy(-Constants.POWERUP_COST);
			current.executePowerupEffect(opponent);
		}
		else
			throw new OutOfEnergyException();
	}
	
	public void playTurn() throws InvalidMoveException{
		
		    if (current.isFrozen()) 
		        current.setFrozen(false);
		    else     
		        board.moveMonster(current, rollDice(),getCurrentOpponent());
		    switchTurn();	
	}
	
	private void switchTurn(){
		if(current == player)
			current = opponent;
		else
			current = player;
	}
	
	private boolean checkWinCondition(Monster monster) {
		return monster.getEnergy()>=1000 && monster.getPosition()==99;
	}
	
	public Monster getWinner(){
		if(checkWinCondition(player))
			return player;
		else if (checkWinCondition(opponent))
			return opponent;
		return null;
	}
	
}
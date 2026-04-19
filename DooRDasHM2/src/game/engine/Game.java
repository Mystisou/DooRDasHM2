package game.engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import game.engine.dataloader.DataLoader;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.*;
import game.engine.Board;

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
		ArrayList<Monster> stationed = new ArrayList<>(allMonsters);
		stationed.remove(player);
		stationed.remove(opponent);
		board.setStationedMonsters(stationed);
		board.initializeBoard(DataLoader.readCells());
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
	////
	void usePowerup() throws OutOfEnergyException {
		if (current.getEnergy() > Constants.POWERUP_COST) {
			current.setEnergy(current.getEnergy()-Constants.POWERUP_COST);
			current.executePowerupEffect(opponent);
		}
		else
			throw new OutOfEnergyException("Not enough energy to use powerup.");
	}
	////
	/*void playTurn() throws InvalidMoveException{
		if(!current.isFrozen()) {
			
		}
	}
	*/
	private void switchTurn(){
		if(current == player)
			current = opponent;
		else
			current = player;
	}
	
	private boolean checkWinCondition(Monster monster) {
		return current.getEnergy()>=1000 && current.getPosition()==99;
	}
	
	Monster getWinner(){
		if(checkWinCondition(player))
			return player;
		else if (checkWinCondition(opponent))
			return opponent;
		return null;
	}
	
}
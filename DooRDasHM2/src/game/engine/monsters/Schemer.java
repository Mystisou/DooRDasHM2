package game.engine.monsters;

import java.util.ArrayList;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Role;

public class Schemer extends Monster {
	
	public Schemer(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}
	
	private int stealEnergyFrom(Monster target) {
        int stolenEnergy = Math.min(Constants.SCHEMER_STEAL, target.getEnergy());
        target.setEnergy(target.getEnergy() - stolenEnergy);
        return stolenEnergy;
	}

	//Chain Attack: steals energy from the opponent and all stationed monsters, 
	//gaining a single total steal bonus at the end
	public void executePowerupEffect(Monster opponentMonster) {
		if (getEnergy() >= Constants.POWERUP_COST) {
			alterEnergy(-Constants.POWERUP_COST);
		
			int totalStolenEnergy = 0;
			
			ArrayList<Monster> stationedMonsters = Board.getStationedMonsters();
			for(int i = 0; i < stationedMonsters.size(); i++)
	            	totalStolenEnergy += stealEnergyFrom(stationedMonsters.get(i));
			
			totalStolenEnergy += stealEnergyFrom(opponentMonster);
			
			alterEnergy(totalStolenEnergy);
		}
	}
	
}

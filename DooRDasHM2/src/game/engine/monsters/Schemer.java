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
		boolean wasShielded = target.isShielded();
		target.setShielded(false);       // bypass shield
		target.alterEnergy(-stolenEnergy); // type bonuses still apply
		target.setShielded(wasShielded); // restore shield without consuming it
		return stolenEnergy;
	}

	//Chain Attack: 
	//steals energy from the opponent & all stationed monsters, 
	//gains a single total steal bonus at the end
	public void executePowerupEffect(Monster opponentMonster) {		
		int totalStolenEnergy = 0;

		ArrayList<Monster> stationedMonsters = Board.getStationedMonsters();
		for(int i = 0; i < stationedMonsters.size(); i++)
			totalStolenEnergy += stealEnergyFrom(stationedMonsters.get(i));

		totalStolenEnergy += stealEnergyFrom(opponentMonster);

		alterEnergy(totalStolenEnergy);
	}

}

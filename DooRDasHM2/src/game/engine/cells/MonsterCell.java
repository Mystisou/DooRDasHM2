package game.engine.cells;

import game.engine.monsters.*;

public class MonsterCell extends Cell {
	private Monster cellMonster;

	public MonsterCell(String name, Monster cellMonster) {
		super(name);
		this.cellMonster = cellMonster;
	}

	public Monster getCellMonster() {
		return this.cellMonster;
	}

	public void onLand(Monster landingMonster, Monster opponentMonster) {	
		Monster stationed = getCellMonster();
		if(stationed != null) {
			if(landingMonster.getRole() == stationed.getRole())
				landingMonster.executePowerupEffect(opponentMonster);

			else {
				if(landingMonster.getEnergy() > stationed.getEnergy()) {
					int playerOldEnergy = landingMonster.getEnergy();
					int stationedOldEnergy = stationed.getEnergy();
					int loss = stationedOldEnergy - playerOldEnergy;
					stationed.alterEnergy(-loss);						
					landingMonster.alterEnergy(loss);
				}
			}
		}
	}		
}

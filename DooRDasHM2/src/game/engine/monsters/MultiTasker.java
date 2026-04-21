package game.engine.monsters;

import game.engine.Constants;
import game.engine.Role;

public class MultiTasker extends Monster {
	private int normalSpeedTurns;
	
	public MultiTasker(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
		this.normalSpeedTurns = 0;
	}

	public int getNormalSpeedTurns() {
		return normalSpeedTurns;
	}

	public void setNormalSpeedTurns(int normalSpeedTurns) {
		this.normalSpeedTurns = normalSpeedTurns;
	}
	
	public void move(int distance) {
        if (getNormalSpeedTurns() == 0) 
        	distance /= 2;
        else 
        	setNormalSpeedTurns(getNormalSpeedTurns() - 1);
        
        super.move(distance);
    }

	//Focus Mode: moves at normal speed for 2 turns
    public void executePowerupEffect(Monster opponentMonster) {
        if (getEnergy() >= Constants.POWERUP_COST) {
            alterEnergy(-Constants.POWERUP_COST);
            setNormalSpeedTurns(2);
        }
    }

}
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
	
	//override move() to normal speed, else half it
	public void move(int distance) {
        if (normalSpeedTurns == 0) 
        	distance /= 2;
        else 
        	normalSpeedTurns--;
        
        super.move(distance);
    }

	//Focus Mode: 
	//sets moves at normal speed for 2 turns to override move()
    public void executePowerupEffect(Monster opponentMonster) {
        if (getEnergy() >= Constants.POWERUP_COST) {
            alterEnergy(-Constants.POWERUP_COST);
            normalSpeedTurns = 2;
        }
    }

}
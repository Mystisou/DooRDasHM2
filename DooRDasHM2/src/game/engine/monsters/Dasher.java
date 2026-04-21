package game.engine.monsters;

import game.engine.Constants;
import game.engine.Role;

public class Dasher extends Monster {
	private int momentumTurns;

	public Dasher(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
		this.momentumTurns = 0;
	}
	
	public int getMomentumTurns() {
		return momentumTurns;
	}
	
	public void setMomentumTurns(int momentumTurns) {
		this.momentumTurns = momentumTurns;
	}
	
	//overrides move() to 3× speed instead of the usual 2×
    public void move(int distance) {
        int speedMultipliedBy;
        if (momentumTurns > 0) {
        	speedMultipliedBy = 3;
        	momentumTurns--;
        }
        else	
        	speedMultipliedBy = 2;
        
        super.move(distance * speedMultipliedBy);        	
    }
    
    //Momentum Rush: 
    // sets how many turns to triple the speed to override move()
    public void executePowerupEffect(Monster opponentMonster) {
    	 if (getEnergy() >= Constants.POWERUP_COST) {
             alterEnergy(-Constants.POWERUP_COST);
             momentumTurns = 3;
         }
    }

}
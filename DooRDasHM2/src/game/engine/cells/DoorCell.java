package game.engine.cells;

import java.util.ArrayList;

import game.engine.Board;
import game.engine.Role;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class DoorCell extends Cell implements CanisterModifier {
	private Role role;
	private int energy;
	private boolean activated;
	
	public DoorCell(String name, Role role, int energy) {
		super(name);
		this.role = role;
		this.energy = energy;
		this.activated = false;
	}
	
	public Role getRole() {
		return role;
	}
	
	public int getEnergy() {
		return energy;
	}
	
	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean isActivated) {
		this.activated = isActivated;
	}
	
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		if(monster.getRole()==this.getRole())
		    monster.alterEnergy(canisterValue);
		else 
			monster.alterEnergy(-canisterValue);
	}
	
	public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);
		if(!isActivated()) {
		  if(landingMonster.isShielded() && landingMonster.getRole()!=getRole())
			  landingMonster.setShielded(false);
		  else {
		  boolean energyChanged = false ;
		  int oldEnergy = landingMonster.getEnergy();
		  modifyCanisterEnergy(landingMonster,getEnergy());
		  int newEnergy = landingMonster.getEnergy();
		   if(oldEnergy!=newEnergy)
			   energyChanged = true;
		  ArrayList<Monster>stationed = Board.getStationedMonsters();
    	  for(Monster m : stationed) {
    		 if(m.getRole()==landingMonster.getRole()) {
    		   int oldE = m.getEnergy();
    		   modifyCanisterEnergy(m,getEnergy());
    		   int newE = m.getEnergy();
    		   if(oldE!=newE)
    			   energyChanged = true;
    		}
    	}
    	    if(energyChanged)
    		setActivated(true);
    		
	      }
	    }
	}
}

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
		  modifyCanisterEnergy(landingMonster,getEnergy());
		  ArrayList<Monster>stationed = Board.getStationedMonsters();
    	  for(Monster m : stationed) {
    		 if(m.getRole()==landingMonster.getRole()) {
    		   int oldEnergy = m.getEnergy();
    		   modifyCanisterEnergy(m,getEnergy());
    		   int newEnergy = m.getEnergy();
    		   if(oldEnergy!=newEnergy)
    			   energyChanged = true;
    		}
    	}
    	    if(energyChanged)
    		setActivated(true);
    		
	      }
	    }
	}
	/*public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);

        if (!isActivated()) {            
            if (landingMonster.getRole() != this.getRole()) {
            	if(landingMonster.isShielded()) 
            		landingMonster.setShielded(false);
            	else {
            		modifyCanisterEnergy(landingMonster,-getEnergy());
            		ArrayList<Monster>stationed = Board.getStationedMonsters();
                	for(Monster m : stationed) {
                		if(m.getRole()==landingMonster.getRole())
                		modifyCanisterEnergy(m,-getEnergy());
                	}
                    this.setActivated(true);
            	}
            }
            else {
            	modifyCanisterEnergy(landingMonster,getEnergy());
            	ArrayList<Monster>stationed = Board.getStationedMonsters();
            	for(Monster m : stationed) {
            		if(m.getRole()==landingMonster.getRole())
            		modifyCanisterEnergy(m,getEnergy());
            	}
                this.setActivated(true);
            }
       }
	}
	
	/*public void onLand(Monster landingMonster, Monster opponentMonster) {
		super.onLand(landingMonster, opponentMonster);

        if (!isActivated()) {            
            if (landingMonster.getRole() != this.getRole()) {
            	if(landingMonster.isShielded()) 
            		landingMonster.setShielded(false);
            	else
            		modifyCanisterEnergy(landingMonster,-getEnergy());
            	else {
            		
            		ArrayList<Monster>stationed = Board.getStationedMonsters();
                	for(Monster m : stationed) {
                		modifyCanisterEnergy(m,-getEnergy());
                	}
                    this.setActivated(true);
            	}
            }
            else {
            	modifyCanisterEnergy(landingMonster,getEnergy());
            	ArrayList<Monster>stationed = Board.getStationedMonsters();
            	for(Monster m : stationed) {
            		modifyCanisterEnergy(m,getEnergy());
            	}
                this.setActivated(true);
            }
       }
	}
    */
}

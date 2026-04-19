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
		monster.alterEnergy(canisterValue);
	}
	
	public void onLand(Monster landingMonster, Monster opponentMonster) {
        super.onLand(landingMonster, opponentMonster);
        
        if (!this.isActivated()) {
            int energyChange = this.getEnergy();
            
            if (landingMonster.getRole() != this.getRole()) 
                energyChange = -energyChange;
            

            boolean energyWasModified = false;

            int oldLandingEnergy = landingMonster.getEnergy();
            this.modifyCanisterEnergy(landingMonster, energyChange);
            
            if (oldLandingEnergy != landingMonster.getEnergy()) 
            	energyWasModified = true;
            

            ArrayList<Monster> stationed = Board.getStationedMonsters();
            
            for (int i = 0; i < stationed.size(); i++) {
                Monster m = stationed.get(i);
                
                if (m.getRole() == landingMonster.getRole()) {
                    int oldStationedEnergy = m.getEnergy();
                    this.modifyCanisterEnergy(m, energyChange);
                    
                    if (oldStationedEnergy != m.getEnergy()) 
                    	energyWasModified = true;
                    
                }
            }

            if (energyWasModified) {
                this.setActivated(true);
            }
        }
    }
	

}

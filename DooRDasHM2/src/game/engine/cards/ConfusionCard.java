package game.engine.cards;

import game.engine.Role;
import game.engine.monsters.Monster;

public class ConfusionCard extends Card {
	private int duration;
	
	public ConfusionCard(String name, String description, int rarity, int duration) {
		super(name, description, rarity, false);
		this.duration = duration;
	}
	
	public void performAction(Monster player, Monster opponent) {
		if(player.getRole() == Role.SCARER) 
			player.setRole(Role.LAUGHER);
		else 
			player.setRole(Role.SCARER);
		
		if(opponent.getRole() == Role.SCARER) 
			opponent.setRole(Role.LAUGHER);
		else 
			opponent.setRole(Role.SCARER);
		
		
		player.setConfusionTurns(this.getDuration());
		opponent.setConfusionTurns(this.getDuration());
		
	}
	
	public int getDuration() {
		return duration;
	}

}

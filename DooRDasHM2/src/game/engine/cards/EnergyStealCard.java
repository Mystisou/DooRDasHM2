package game.engine.cards;

import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class EnergyStealCard extends Card implements CanisterModifier {
	private int energy;

	public EnergyStealCard(String name, String description, int rarity, int energy) {
		super(name, description, rarity, true);
		this.energy = energy;
	}
	
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
		monster.alterEnergy(canisterValue);
	}
	
	public void performAction(Monster player, Monster opponent) {
		int amountToSteal = Math.min(opponent.getEnergy(), this.getEnergy());
		int OldEnergy = opponent.getEnergy();
		this.modifyCanisterEnergy(opponent, -amountToSteal);
		int actualStolenAmount = OldEnergy - opponent.getEnergy();
		this.modifyCanisterEnergy(player, actualStolenAmount);
	}
	
	public int getEnergy() {
		return energy;
	}
	
}

package game.engine.cards;

import game.engine.monsters.Monster;

public class ShieldCard extends Card {
	
	public ShieldCard(String name, String description, int rarity) {
		super(name, description, rarity, true); 
	}

	@Override
	public void performAction(Monster landingMonster, Monster opponentMonster) {
		// TODO Auto-generated method stub
		
	}

}

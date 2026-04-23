package game.engine.cards;

import game.engine.monsters.Monster;

public class SwapperCard extends Card {

	public SwapperCard(String name, String description, int rarity) {
		super(name, description, rarity, true);
	}

	@Override
	public void performAction(Monster landingMonster, Monster opponentMonster) {
		if (landingMonster.getPosition()<  opponentMonster.getPosition()) {
			int temp=landingMonster.getPosition();
			landingMonster.setPosition(opponentMonster.getPosition());
			opponentMonster.setPosition(landingMonster.getPosition());
			
		}
		
	}
	
}

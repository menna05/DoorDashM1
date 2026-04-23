package game.engine.cards;

import game.engine.monsters.Monster;

public class ConfusionCard extends Card {
	private int duration;
	
	public ConfusionCard(String name, String description, int rarity, int duration) {
		super(name, description, rarity, false);
		this.duration = duration;
	}
	
	public int getDuration() {
		return duration;
	}

	@Override
	public void performAction(Monster landingMonster, Monster opponentMonster) {
		// TODO Auto-generated method stub
		
	}

}

package game.engine.monsters;

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
	public void executePowerupEffect(Monster opponentMonster) {
		setMomentumTurns(3);
	}
		public int getMovementMultiplier() {
	        if (momentumTurns > 0) {
	            momentumTurns--;
	            return 3;
	        }
	        return 2;
	    }
	
	}

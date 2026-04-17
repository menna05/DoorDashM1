package game.engine.monsters;

import game.engine.Constants;
import game.engine.Role;

 public class Schemer extends Monster {
	
	public Schemer(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
	}
	private int stealEnergyFrom(Monster target) {
		 int stealAmount = Math.min(target.getEnergy(), Constants.SCHEMER_STEAL);
		    target.alterEnergy(-stealAmount);
		    return stealAmount;
	}
	
	public  void executePowerupEffect(Monster opponentMonster) {
		
		    int stolen = stealEnergyFrom(opponentMonster);

		    this.alterEnergy(stolen);
	}
	}
	


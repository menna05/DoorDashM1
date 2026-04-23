package game.engine.monsters;

import game.engine.Role;

public class MultiTasker extends Monster {
	private int normalSpeedTurns;
	
	public MultiTasker(String name, String description, Role role, int energy) {
		super(name, description, role, energy);
		this.normalSpeedTurns = 0;
	}

	public int getNormalSpeedTurns() {
		return normalSpeedTurns;
	}

	public void setNormalSpeedTurns(int normalSpeedTurns) {
		this.normalSpeedTurns = normalSpeedTurns;
	}
	public void executePowerupEffect(Monster opponentMonster) {
		
		 normalSpeedTurns = 2;
    }

	public double getMovementMultiplier() {
	    if (  normalSpeedTurns> 0) {
	    	 normalSpeedTurns--;
	        return 1.0; 
	    }
	    return 0.5; 
	}
    public void setEnergy(int energy) {
       
        super.setEnergy(energy + 200);
    }
}

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
		
		 this.normalSpeedTurns = 2;
    }

   
    public void setEnergy(int energy) {
       
        super.setEnergy(energy + 200);
    }
}

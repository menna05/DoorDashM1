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

	@Override
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
	    if (monster.getRole() == role) {
	        monster.alterEnergy(canisterValue);
	    } else {
	        monster.alterEnergy(-canisterValue);
	    }
	}
	
	@Override
	public void onLand(Monster landingMonster, Monster opponentMonster) {
	    super.onLand(landingMonster, opponentMonster);
	    if (!activated) {
	        int energyChange = (landingMonster.getRole() == role) ? energy : -energy;
	        
	        int before = landingMonster.getEnergy();
	        modifyCanisterEnergy(landingMonster, energy);
	        boolean landingChanged = landingMonster.getEnergy() != before;
	        boolean shieldBlocked = landingMonster.isShielded() == false && before == landingMonster.getEnergy() && energyChange < 0;
	        
	        boolean changed = landingChanged;
	        
	        if (!shieldBlocked) {
	            for (Monster m : Board.getStationedMonsters()) {
	                if (m.getRole() == landingMonster.getRole()) {
	                    int mBefore = m.getEnergy();
	                    modifyCanisterEnergy(m, energy);
	                    if (m.getEnergy() != mBefore) changed = true;
	                }
	            }
	        }
	        if (changed) {
	            activated = true;
	        }
	    }
	}

	     	}


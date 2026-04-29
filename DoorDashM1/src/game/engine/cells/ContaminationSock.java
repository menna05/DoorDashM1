package game.engine.cells;

import game.engine.Constants;
import game.engine.interfaces.CanisterModifier;
import game.engine.monsters.Monster;

public class ContaminationSock extends TransportCell implements CanisterModifier {

	public ContaminationSock(String name, int effect) {
		super(name,  effect);
	}

	@Override
	public void modifyCanisterEnergy(Monster monster, int canisterValue) {
	    monster.alterEnergy(canisterValue);
	}
	
	@Override
	public void onLand(Monster landingMonster, Monster opponentMonster) {
	    super.onLand(landingMonster, opponentMonster); // calls TransportCell's onLand which calls transport
	    
	}
	
	@Override
	public void transport(Monster monster) {
	    super.transport(monster); // moves the monster
	    modifyCanisterEnergy(monster, -Constants.SLIP_PENALTY);
	}

}


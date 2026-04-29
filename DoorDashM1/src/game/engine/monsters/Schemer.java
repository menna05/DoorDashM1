package game.engine.monsters;

import game.engine.Board;
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
		       int totalStolen=0;
		      totalStolen += stealEnergyFrom(opponentMonster);
		      for (Monster m : Board.getStationedMonsters()) {
		          
		          if (m != opponentMonster) { 
		              totalStolen += stealEnergyFrom(m);
		          }
		      }
		   
	           super.alterEnergy(totalStolen);
	}
	public int modifyIncomingEnergy(int energy) {
	    
	        return energy + Constants.SCHEMER_STEAL;
	   
	}
	
 }
	


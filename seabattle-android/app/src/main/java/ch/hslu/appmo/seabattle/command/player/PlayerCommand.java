package ch.hslu.appmo.seabattle.command.player;

import java.util.ArrayList;

import ch.hslu.appmo.seabattle.command.Command;

public abstract class PlayerCommand extends Command {
	
	private final PlayerCommandType fCommandType;
	
	public PlayerCommand(PlayerCommandType commandType) {
		fCommandType = commandType;	
	}
	
	public abstract ArrayList<Object> getParams();
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(fCommandType.ordinal());
		
		for (Object param : getParams()) {
			builder.append(PARAM_SEPERATOR);
			builder.append(param);
		}
		
		builder.append(LINE_BREAK);
		
		return builder.toString();
	}

	public PlayerCommandType getfCommandType() {
		return fCommandType;
	}
}

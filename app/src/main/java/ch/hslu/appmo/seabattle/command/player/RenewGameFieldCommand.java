package ch.hslu.appmo.seabattle.command.player;

import java.util.ArrayList;

public class RenewGameFieldCommand extends PlayerCommand {
	
	public RenewGameFieldCommand() {
		super(PlayerCommandType.RenewGameField);
	}

	@Override
	public ArrayList<Object> getParams() {
		return new ArrayList<Object>();
	}
}

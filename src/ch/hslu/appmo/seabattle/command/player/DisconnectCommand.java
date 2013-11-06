package ch.hslu.appmo.seabattle.command.player;

import java.util.ArrayList;

public class DisconnectCommand extends PlayerCommand {

	public DisconnectCommand() {
		super(PlayerCommandType.Disconnect);
	}

	@Override
	public ArrayList<Object> getParams() {
		return new ArrayList<Object>();
	}
}

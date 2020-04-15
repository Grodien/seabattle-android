package ch.hslu.appmo.seabattle.command.player;

import java.util.ArrayList;

public class KeepAlivePlayerCommand extends PlayerCommand {

	public KeepAlivePlayerCommand() {
		super(PlayerCommandType.KeepAlive);
	}

	@Override
	public ArrayList<Object> getParams() {
		return new ArrayList<Object>();
	}
}

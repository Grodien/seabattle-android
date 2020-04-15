package ch.hslu.appmo.seabattle.command.player;

import java.util.ArrayList;

public class GetServerSettingsCommand extends PlayerCommand {

	public GetServerSettingsCommand() {
		super(PlayerCommandType.GetServerSettings);
	}

	@Override
	public ArrayList<Object> getParams() {
		return new ArrayList<Object>();
	}
}

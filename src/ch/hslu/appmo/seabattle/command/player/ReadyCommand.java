package ch.hslu.appmo.seabattle.command.player;

import java.util.ArrayList;

public class ReadyCommand extends PlayerCommand {

	private boolean fReady;

	public ReadyCommand(boolean isReady) {
		super(PlayerCommandType.Ready);
		fReady = isReady;
	}

	@Override
	public ArrayList<Object> getParams() {
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(String.valueOf(fReady));
		
		return list;
	}

	public boolean isReady() {
		return fReady;
	}

	public void setIsReady(boolean isReady) {
		fReady = isReady;
	}
}

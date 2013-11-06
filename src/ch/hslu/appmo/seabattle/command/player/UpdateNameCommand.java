package ch.hslu.appmo.seabattle.command.player;

import java.util.ArrayList;

public class UpdateNameCommand extends PlayerCommand {

	private String fNewName;
	
	public UpdateNameCommand(String newName) {
		super(PlayerCommandType.UpdateName);
		fNewName = newName;
	}

	public String getNewName() {
		return fNewName;
	}

	public void setNewName(String newName) {
		fNewName = newName;
	}

	@Override
	public ArrayList<Object> getParams() {
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(fNewName);
		
		return list;
	}

}

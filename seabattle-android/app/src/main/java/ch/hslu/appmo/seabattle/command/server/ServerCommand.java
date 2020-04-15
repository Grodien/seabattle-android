package ch.hslu.appmo.seabattle.command.server;

import java.util.ArrayList;

import ch.hslu.appmo.seabattle.command.Command;

public abstract class ServerCommand extends Command {
	private final ServerCommandType fCommandType;
	
	protected ArrayList<Object> fParams;
	
	public ServerCommand(ServerCommandType commandType, String[] params) {
		fCommandType = commandType;	
		fParams = new ArrayList<Object>();
		parseParams(params);
	}
	
	public ServerCommandType getCommandType() {
		return fCommandType;
	}
	
	protected abstract void parseParams(String[] params);
}

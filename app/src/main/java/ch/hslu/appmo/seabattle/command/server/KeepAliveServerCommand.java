package ch.hslu.appmo.seabattle.command.server;


public class KeepAliveServerCommand extends ServerCommand {

	
	public KeepAliveServerCommand(String... params) {
		super(ServerCommandType.KeepAlive, params);
	}

	@Override
	protected void parseParams(String[] params) {
		// No Params
	}
}

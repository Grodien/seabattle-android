package ch.hslu.appmo.seabattle.command.server;


public class PlayerFoundCommand extends ServerCommand {

	public PlayerFoundCommand(String... params) {
		super(ServerCommandType.PlayerFound, params);
	}
	
	public String getPlayerName() {
		return (String) fParams.get(0);
	}

	@Override
	protected void parseParams(String[] params) {
		fParams.add(params[1]);
	}

}

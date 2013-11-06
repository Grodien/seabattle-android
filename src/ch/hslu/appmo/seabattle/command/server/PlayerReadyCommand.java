package ch.hslu.appmo.seabattle.command.server;


public class PlayerReadyCommand extends ServerCommand {

	public PlayerReadyCommand(String... params) {
		super(ServerCommandType.PlayerReady, params);
	}

	public boolean getRdyState() {
		return (Boolean)fParams.get(0);
	}
	
	public boolean getStartGame() {
		return (Boolean)fParams.get(1);
	}
	
	public boolean getMyTurn() {
		return (Boolean)fParams.get(2);
	}
	
	@Override
	protected void parseParams(String[] params) {
		fParams.add(Boolean.parseBoolean(params[1]));
		fParams.add(Boolean.parseBoolean(params[2]));
		fParams.add(Boolean.parseBoolean(params[3]));
	}

}

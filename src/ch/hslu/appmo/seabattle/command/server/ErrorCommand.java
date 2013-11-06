package ch.hslu.appmo.seabattle.command.server;


public class ErrorCommand extends ServerCommand {
	public static final int ERROR_CODE_PLAYER_DISCONNECTED = 1;
	
	public ErrorCommand(String... params) {
		super(ServerCommandType.Error, params);
	}

	public int getErrorCode() {
		return (Integer) fParams.get(0);
	}
	
	@Override
	protected void parseParams(String[] params) {
		fParams.add(Integer.parseInt(params[1]));
	}

}

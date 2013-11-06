package ch.hslu.appmo.seabattle.command.server;


public class WinCommand extends ServerCommand {

	public WinCommand(String... params) {
		super(ServerCommandType.Win, params);
	}
	
	public boolean isWin() {
		return (Boolean)fParams.get(0);
	}
	
	@Override
	protected void parseParams(String[] params) {
		fParams.add(Boolean.parseBoolean(params[1]));
	}

}

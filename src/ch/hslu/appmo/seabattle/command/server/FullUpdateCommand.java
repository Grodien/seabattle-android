package ch.hslu.appmo.seabattle.command.server;


public class FullUpdateCommand extends ServerCommand {

	public FullUpdateCommand(String... params) {
		super(ServerCommandType.FullUpdate, params);
	}

	public boolean isMyField() {
		return (Boolean)fParams.get(1);
	}

	public boolean isMyTurn() {
		return (Boolean)fParams.get(2);
	}

	public String getFieldData() {
		return (String)fParams.get(0);
	}

	@Override
	protected void parseParams(String[] params) {
		fParams.add(params[1]);
		fParams.add(Boolean.parseBoolean(params[2]));
		fParams.add(Boolean.parseBoolean(params[3]));
	}

}

package ch.hslu.appmo.seabattle.command.server;


public class PartialUpdateCommand extends ServerCommand {

	public PartialUpdateCommand(String... params) {
		super(ServerCommandType.PartialUpdate, params);
	}

	public int getPosX() {
		return (Integer) fParams.get(0);
	}

	public int getPosY() {
		return (Integer) fParams.get(1);
	}

	public int getNewValue() {
		return (Integer) fParams.get(2);
	}

	public boolean isMyField() {
		return (Boolean) fParams.get(3);
	}

	public boolean isMyTurn() {
		return (Boolean) fParams.get(4);
	}

	@Override
	protected void parseParams(String[] params) {
		fParams.add(Integer.parseInt(params[1]));
		fParams.add(Integer.parseInt(params[2]));
		fParams.add(Integer.parseInt(params[3]));
		fParams.add(Boolean.parseBoolean(params[4]));
		fParams.add(Boolean.parseBoolean(params[5]));
	}

}

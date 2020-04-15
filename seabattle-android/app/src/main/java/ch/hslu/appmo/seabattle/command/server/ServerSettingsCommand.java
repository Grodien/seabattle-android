package ch.hslu.appmo.seabattle.command.server;


public class ServerSettingsCommand extends ServerCommand {

	public ServerSettingsCommand(String... params) {
		super(ServerCommandType.ServerSettings, params);
	}

	public int getSize() {
		return (Integer)fParams.get(0);
	}
	
	public int getSmallShipCount() {
		return (Integer)fParams.get(1);
	}
	
	public int getMediumShipCount() {
		return (Integer)fParams.get(2);
	}
	
	public int getBigShipCount() {
		return (Integer)fParams.get(3);
	}
	
	public int getHugeShipCount() {
		return (Integer)fParams.get(4);
	}
	
	public int getUltimateShipCount() {
		return (Integer)fParams.get(5);
	}

	@Override
	protected void parseParams(String[] params) {
		fParams.add(Integer.parseInt(params[1]));
		fParams.add(Integer.parseInt(params[2]));
		fParams.add(Integer.parseInt(params[3]));
		fParams.add(Integer.parseInt(params[4]));
		fParams.add(Integer.parseInt(params[5]));
		fParams.add(Integer.parseInt(params[6]));
	}
}

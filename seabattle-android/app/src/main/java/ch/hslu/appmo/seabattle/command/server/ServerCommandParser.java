package ch.hslu.appmo.seabattle.command.server;


public abstract class ServerCommandParser {
	public static ServerCommand parseCommand(String... params) {
		ServerCommandType type = ServerCommandType.values()[Integer.parseInt(params[0])];
		
		switch (type) {
			case KeepAlive:
				return new KeepAliveServerCommand(params);			
	
			case FullUpdate:
				return new FullUpdateCommand(params);
				
			case PartialUpdate:
				return new PartialUpdateCommand(params);
				
			case PlayerFound:
				return new PlayerFoundCommand(params);
	
			case PlayerReady:
				return new PlayerReadyCommand(params);
				
			case ServerSettings:
				return new ServerSettingsCommand(params);
				
			case Win:
				return new WinCommand(params);
				
			case Error:
				return new ErrorCommand(params);
				
			default:			
				return null;
		}
	}
}

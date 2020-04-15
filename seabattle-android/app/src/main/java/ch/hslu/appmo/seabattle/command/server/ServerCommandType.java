package ch.hslu.appmo.seabattle.command.server;

public enum ServerCommandType {
	PartialUpdate,
	FullUpdate,
	PlayerReady,
	ServerSettings,
	Error,
	Win,
	PlayerFound,
	KeepAlive
}

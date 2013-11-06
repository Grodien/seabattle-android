package ch.hslu.appmo.seabattle.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import ch.hslu.appmo.seabattle.command.Command;
import ch.hslu.appmo.seabattle.command.player.KeepAlivePlayerCommand;
import ch.hslu.appmo.seabattle.command.player.PlayerCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommandHandler;
import ch.hslu.appmo.seabattle.command.server.ServerCommandParser;
import ch.hslu.appmo.seabattle.command.server.ServerCommandType;


public class TCPClient extends Thread implements ServerCommandHandler {

	private static final String HOST_NAME = "localhost";
	private static final int TCP_PORT = 8222;
	
	private static TCPClient instance = null;
	/**
	 * Retrieves the Server Instance or creates it if necessary. 
	 * @return the Server instance.
	 */
	public static synchronized TCPClient getInstance() {
		if (instance == null) {
			instance = new TCPClient();
		}
		return instance;
	}
	
	private Socket fSocket;
	private BufferedReader fInputStream;
	private BufferedWriter fOutputStream;
	private LinkedBlockingQueue<String> fOutputQueue;
	private Map<ServerCommandType, ServerCommandHandler> fHandlers;
	private boolean fShouldShutdown = false;
	
	private TCPClient() {
		super();		
		fHandlers = new HashMap<ServerCommandType, ServerCommandHandler>();
	}
	
	public void sendCommand(PlayerCommand command) {
		fOutputQueue.offer(command.toString());
	}
	
	public void connect() {
		fShouldShutdown = false;
		this.start();
	}
	
	public void disconnect() {
		fShouldShutdown = true;
		fOutputQueue.clear();
	}
	
	public void subscribeToCommand(ServerCommandType type, ServerCommandHandler handler) {
		fHandlers.put(type, handler);
	}
	
	@Override
	public void run() {
		try {
			fSocket = new Socket(HOST_NAME, TCP_PORT);
			System.out.println("Connection established");
			
			subscribeToCommand(ServerCommandType.KeepAlive, this);
			
			fInputStream = new BufferedReader(new InputStreamReader(fSocket.getInputStream()));
			fOutputStream = new BufferedWriter(new OutputStreamWriter(fSocket.getOutputStream()));
			
			while (!fSocket.isClosed()) {
				if (fShouldShutdown) {
					System.out.println("Closing Socket");
					fSocket.close();
					break;
				}
				
				if (fInputStream.ready()) {
					String[] params = fInputStream.readLine().split(Command.PARAM_SEPERATOR);
					
					ServerCommand command = ServerCommandParser.parseCommand(params);
					ServerCommandHandler commandHandler = fHandlers.get(command.getCommandType());
					if (commandHandler != null) {
						commandHandler.handleCommand(command);
					}
				}
				
				if (!fOutputQueue.isEmpty()) {
					String message = fOutputQueue.poll();
					if (message != null) {
						fOutputStream.write(message);
						fOutputStream.flush();
					}
				}
			}			
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Failed to connect to Server");
		}
		
		System.out.println("Socket closed!");
	}

	@Override
	public void handleCommand(ServerCommand command) {
		sendCommand(new KeepAlivePlayerCommand());
	}
}

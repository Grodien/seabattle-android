package ch.hslu.appmo.seabattle.network;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.hslu.appmo.seabattle.command.Command;
import ch.hslu.appmo.seabattle.command.player.PlayerCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommandHandler;
import ch.hslu.appmo.seabattle.command.server.ServerCommandParser;
import ch.hslu.appmo.seabattle.command.server.ServerCommandType;
import ch.hslu.appmo.seabattle.models.PlayerSettings;

public class ParseClient {

    private static ParseClient instance = null;
    private Map<ServerCommandType, ServerCommandHandler> handlers;
    private ParseLiveQueryClient parseLiveQueryClient = null;
    private List<ServerCommand> unhandledCommands = new ArrayList<>();

    public static synchronized ParseClient getInstance() {
        if (instance == null) {
            instance = new ParseClient();
        }
        return instance;
    }

    private ParseClient() {
        super();
        handlers = new HashMap<>();
        initLiveQuery();
    }

    private void initLiveQuery() {
        try {
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://seabattle.back4app.io/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> parseQuery = new ParseQuery<>("Messages");
        parseQuery.whereEqualTo("playerId", PlayerSettings.getInstance(null).getPlayerId());
        SubscriptionHandling<ParseObject> subscription = parseLiveQueryClient.subscribe(parseQuery);
        subscription.handleEvent(SubscriptionHandling.Event.CREATE, new SubscriptionHandling.HandleEventCallback<ParseObject>() {
            @Override
            public void onEvent(ParseQuery<ParseObject> query, final ParseObject object) {
                String[] params = object.getString("content").split(Command.PARAM_SEPERATOR);

                ServerCommand command = ServerCommandParser.parseCommand(params);
                ServerCommandHandler commandHandler = handlers.get(command.getCommandType());
                if (commandHandler != null) {
                    commandHandler.handleCommand(command);
                } else {
                    System.out.println("No handler for " + command.getCommandType().toString());
                    unhandledCommands.add(command);
                }
            }
        });
    }

    public void subscribeToCommand(ServerCommandType type, ServerCommandHandler handler) {
        handlers.put(type, handler);

        for (int i = 0; i < unhandledCommands.size(); i ++) {
            ServerCommand serverCommand = unhandledCommands.get(i);
            if (type.equals(serverCommand.getCommandType())) {
                handler.handleCommand(serverCommand);
                unhandledCommands.remove(i--);
            }
        }
    }

    public void queueOnline() {
        initLiveQuery();

        HashMap<String, Object> params = new HashMap<>();
        params.put("playerId", PlayerSettings.getInstance(null).getPlayerId());
        params.put("playerName", PlayerSettings.getInstance(null).getPlayerName());
        ParseCloud.callFunctionInBackground("queue", params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    // Success
                }
            }
        });
    }

    public void dequeue() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("playerId", PlayerSettings.getInstance(null).getPlayerId());
        ParseCloud.callFunctionInBackground("dequeue", params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    // Success
                }
            }
        });
    }

    public void sendCommand(PlayerCommand command) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("playerId", PlayerSettings.getInstance(null).getPlayerId());
        params.put("content", command.toString());
        ParseCloud.callFunctionInBackground(command.getfCommandType().name(), params, new FunctionCallback<String>() {
            public void done(String result, ParseException e) {
                if (e == null) {
                    // Success
                }
            }
        });
    }
}

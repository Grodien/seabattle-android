package ch.hslu.appmo.seabattle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import ch.hslu.appmo.seabattle.command.player.UpdateNameCommand;
import ch.hslu.appmo.seabattle.command.server.PlayerFoundCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommandHandler;
import ch.hslu.appmo.seabattle.command.server.ServerCommandType;
import ch.hslu.appmo.seabattle.command.server.ServerSettingsCommand;
import ch.hslu.appmo.seabattle.models.Game;
import ch.hslu.appmo.seabattle.models.GameSettings;
import ch.hslu.appmo.seabattle.models.Player;
import ch.hslu.appmo.seabattle.models.PlayerSettings;
import ch.hslu.appmo.seabattle.network.TCPClient;


public class MainActivity extends Activity implements ServerCommandHandler {

	private Button fPlayOnlineButton;
	private Button fSettingsButton;
	private TCPClient fTCPClient;
	private ProgressDialog fProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		PlayerSettings.getInstance(this);
		
		fPlayOnlineButton = (Button)findViewById(R.id.btnPlayOnline);
		fSettingsButton = (Button)findViewById(R.id.btnSettings);
		
		fPlayOnlineButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!PlayerSettings.getInstance(null).hasSettings()) {
					AlertDialog.Builder builder = new Builder(MainActivity.this);
					builder.setTitle("Name Missing")
					.setMessage("You need to set a Name before you can play online")
					.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
							startActivity(intent);
						}
					}).show();
				} else {
					startGameSearch();
				}
			}
		});
		
		fSettingsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});
	}
	
	public void startGameSearch() {
		fTCPClient = TCPClient.getInstance();
		
		fTCPClient.subscribeToCommand(ServerCommandType.ServerSettings, this);
		fTCPClient.subscribeToCommand(ServerCommandType.PlayerFound, this);
		fTCPClient.subscribeToCommand(ServerCommandType.Error, this);
		
		fTCPClient.connect();
		
		fTCPClient.sendCommand(new UpdateNameCommand(PlayerSettings.getInstance(MainActivity.this).getPlayerName()));
		
		fProgressDialog = new ProgressDialog(MainActivity.this);
		fProgressDialog.setMessage("Searching for Player...");
        fProgressDialog.setCancelable(true);
        fProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				fTCPClient.disconnect();
			}
		});      
        
        runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				 fProgressDialog.show();
			}
		});
       
	}

	@Override
	public void handleCommand(ServerCommand command) {
		switch (command.getCommandType()) {
			case ServerSettings:
				ServerSettingsCommand cmd = (ServerSettingsCommand)command;
				GameSettings.SIZE = cmd.getSize();
				GameSettings.SMALL = cmd.getSmallShipCount();
				GameSettings.MEDIUM = cmd.getMediumShipCount();
				GameSettings.BIG = cmd.getBigShipCount();
				GameSettings.HUGE = cmd.getHugeShipCount();
				GameSettings.ULTIMATE = cmd.getUltimateShipCount();
				break;
	
			case PlayerFound:
				PlayerFoundCommand foundCmd = (PlayerFoundCommand)command;
				Player p1 = new Player(PlayerSettings.getInstance(null).getPlayerName());
				Player p2 = new Player(foundCmd.getPlayerName());
				
				Game.getInstance().newGame(p1, p2);
				
				fProgressDialog.dismiss();
				Intent intent = new Intent(MainActivity.this, GameSetupActivity.class);
				startActivity(intent);
				break;
				
			case Error:
				fProgressDialog.cancel();
				break;
				
			default:
				System.out.println("Unexpected Command received");
				break;
		}
	}
}

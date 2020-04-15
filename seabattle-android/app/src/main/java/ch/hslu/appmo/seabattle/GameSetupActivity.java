package ch.hslu.appmo.seabattle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.hslu.appmo.seabattle.command.player.ReadyCommand;
import ch.hslu.appmo.seabattle.command.player.RenewGameFieldCommand;
import ch.hslu.appmo.seabattle.command.server.FullUpdateCommand;
import ch.hslu.appmo.seabattle.command.server.PlayerFoundCommand;
import ch.hslu.appmo.seabattle.command.server.PlayerReadyCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommandHandler;
import ch.hslu.appmo.seabattle.command.server.ServerCommandType;
import ch.hslu.appmo.seabattle.models.Game;
import ch.hslu.appmo.seabattle.network.ParseClient;
import ch.hslu.appmo.seabattle.network.TCPClient;
import ch.hslu.appmo.seabattle.view.PlayFieldView;

public class GameSetupActivity extends Activity implements ServerCommandHandler {

	private static final int HEIGHT_ADJUSTMENT = 120;
	private int fWidth;
	private int fHeight;
	//private TCPClient fTCPClient;
	private ParseClient parseClient;
	private RelativeLayout fLayout;
	private PlayFieldView fPlayFieldView;
	private Button fBtnReadyUp;
	private Game fGameInstance;
	private TextView fEnemyLabel;
	private TextView fEnemyReady;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_game_setup);
		
		fLayout = (RelativeLayout) findViewById(R.id.relLayout);
		fBtnReadyUp = (Button) findViewById(R.id.btnReadyUp);
		fEnemyLabel = (TextView) findViewById(R.id.txtEnemy);
		fEnemyReady = (TextView) findViewById(R.id.txtReady);
		
		//fTCPClient = TCPClient.getInstance();
		parseClient = ParseClient.getInstance();
		
		//fTCPClient.subscribeToCommand(ServerCommandType.FullUpdate, this);
		parseClient.subscribeToCommand(ServerCommandType.FullUpdate, this);
		//fTCPClient.subscribeToCommand(ServerCommandType.PlayerReady, this);
		parseClient.subscribeToCommand(ServerCommandType.PlayerReady, this);
		//fTCPClient.subscribeToCommand(ServerCommandType.PlayerFound, this);
		parseClient.subscribeToCommand(ServerCommandType.PlayerFound, this);

		Display display = getWindowManager().getDefaultDisplay();
		fWidth = display.getWidth();
		fHeight = display.getHeight();
		
		int size = fWidth > (fHeight - HEIGHT_ADJUSTMENT) ? (fHeight-HEIGHT_ADJUSTMENT) : fWidth;
		fPlayFieldView = new PlayFieldView(getBaseContext(), size, Game.getInstance().getMe(), false);
		
		fGameInstance = Game.getInstance();

		updateNameLabel();
		updateReadyLabel();
		
		fLayout.addView(fPlayFieldView);


		//fTCPClient.sendCommand(new RenewGameFieldCommand());
		//parseClient.sendCommand(new RenewGameFieldCommand());
	}
	
	public void updateReadyLabel() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				fEnemyReady.setText(fGameInstance.getEnemy().isReady() ? "Ready" : "Not Ready");
			}
		});
	}
	
	public void updateNameLabel() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				fEnemyLabel.setText(fGameInstance.getEnemy().getName() + ": ");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_setup, menu);
		return true;
	}

	@Override
	public void handleCommand(ServerCommand command) {
		switch (command.getCommandType()) {
			case FullUpdate:
				FullUpdateCommand cmd = (FullUpdateCommand)command;
				Game.getInstance().getMe().getPlayfield().updateWithData(cmd.getFieldData());
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						fPlayFieldView.invalidate();
					}
				});
				
				break;
	
			case PlayerReady:
				PlayerReadyCommand readyCmd = (PlayerReadyCommand)command;
				Game.getInstance().getEnemy().setReady(readyCmd.getRdyState());
				updateReadyLabel();
				
				if (readyCmd.getStartGame()) {
					fGameInstance.setTurn(readyCmd.getMyTurn() ? fGameInstance.getMe() : fGameInstance.getEnemy());
					
					Intent intent = new Intent(GameSetupActivity.this, GameActivity.class);
					startActivity(intent);
				}
				break;
				
			case PlayerFound:
				PlayerFoundCommand foundCmd = (PlayerFoundCommand)command;
				fGameInstance.getEnemy().setName(foundCmd.getPlayerName());
				updateNameLabel();
			default:
			break;
		}
	}
	
	public void readyUp(View view) {
		//fTCPClient.sendCommand(new ReadyCommand(true));
		fGameInstance.getMe().setReady(true);
		fBtnReadyUp.setEnabled(false);
	}
	
	public void renewGameField(View view) {
		parseClient.sendCommand(new RenewGameFieldCommand());
		//fTCPClient.sendCommand(new RenewGameFieldCommand());
	}
}

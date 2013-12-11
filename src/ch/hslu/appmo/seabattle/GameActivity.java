package ch.hslu.appmo.seabattle;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.hslu.appmo.seabattle.command.player.PlayerShootCommand;
import ch.hslu.appmo.seabattle.command.server.FullUpdateCommand;
import ch.hslu.appmo.seabattle.command.server.PartialUpdateCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommand;
import ch.hslu.appmo.seabattle.command.server.ServerCommandHandler;
import ch.hslu.appmo.seabattle.command.server.ServerCommandType;
import ch.hslu.appmo.seabattle.command.server.WinCommand;
import ch.hslu.appmo.seabattle.models.Game;
import ch.hslu.appmo.seabattle.models.GameSettings;
import ch.hslu.appmo.seabattle.models.Player;
import ch.hslu.appmo.seabattle.models.PlayerSettings;
import ch.hslu.appmo.seabattle.network.TCPClient;
import ch.hslu.appmo.seabattle.view.PlayFieldView;

public class GameActivity extends Activity implements ServerCommandHandler {

	private RelativeLayout fMyLayout;
	private RelativeLayout fEnemyLayout;
	private TCPClient fTCPClient;
	private int fWidth;
	private int fHeight;
	private PlayFieldView fEnemyPlayFieldView;
	private PlayFieldView fMyPlayFieldView;
	private Game fGameInstance;
	private TextView fTxtStatus;
	private Toast fMyTurnToast;
	private Toast fEnemyTurnToast;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		fMyTurnToast = Toast.makeText(getBaseContext(), "Your Turn", Toast.LENGTH_SHORT);
		fEnemyTurnToast = Toast.makeText(getBaseContext(), "Enemy Turn", Toast.LENGTH_SHORT);
		
		fMyLayout = (RelativeLayout) findViewById(R.id.relMineLayout);
		fEnemyLayout = (RelativeLayout) findViewById(R.id.relEnemy);
		
		fTxtStatus = (TextView) findViewById(R.id.txtStatus);
		fTxtStatus.setVisibility(View.INVISIBLE);
		
		fTCPClient = TCPClient.getInstance();
		fGameInstance = Game.getInstance();
		
		fTCPClient.subscribeToCommand(ServerCommandType.FullUpdate, this);
		fTCPClient.subscribeToCommand(ServerCommandType.PartialUpdate, this);
		fTCPClient.subscribeToCommand(ServerCommandType.Win, this);
		
		Display display = getWindowManager().getDefaultDisplay();
		fWidth = display.getWidth();
		fHeight = display.getHeight();
		
		int size = fWidth > fHeight ? fHeight : fWidth;
		fEnemyPlayFieldView = new PlayFieldView(getBaseContext(), size, Game.getInstance().getEnemy(), true);
		fEnemyLayout.addView(fEnemyPlayFieldView);
		
		int size2 = fHeight - size;
		fMyPlayFieldView = new PlayFieldView(getBaseContext(), size2, Game.getInstance().getMe(), false);
		fMyLayout.addView(fMyPlayFieldView);
		
		updateStatusLabel();
		
		fEnemyPlayFieldView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				
				if (fGameInstance.getTurn() == fGameInstance.getMe()) {
					int fieldSize = arg0.getWidth() / GameSettings.SIZE;
					
					int x = (int) (arg1.getX() / fieldSize);
					int y = (int) (arg1.getY() / fieldSize);
					
					if (x < GameSettings.SIZE && y < GameSettings.SIZE) {
						PlayerShootCommand cmd = new PlayerShootCommand(x,y);
						fTCPClient.sendCommand(cmd);
					}
				}
				
				return true;
			}
		});
	}

	private void updateStatusLabel() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if (fGameInstance.getTurn() == fGameInstance.getMe()) {
					if (!fMyTurnToast.getView().isShown()) {
						fMyTurnToast.show();
					}
					fTxtStatus.setText("My Turn");
				} else {
					if (!fEnemyTurnToast.getView().isShown()) {
						fEnemyTurnToast.show();
					}
					fTxtStatus.setText("Enemy Turn");
				}
			}
		});
	}

	private void updatePlayFields() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				fMyPlayFieldView.invalidate();
				fEnemyPlayFieldView.invalidate();
				updateStatusLabel();
			}
		});
	}
	
	@Override
	public void handleCommand(ServerCommand command) {
		switch (command.getCommandType()) {
			case FullUpdate:
				FullUpdateCommand fullCmd = (FullUpdateCommand)command;
				Player player = fullCmd.isMyField() ? fGameInstance.getMe() : fGameInstance.getEnemy();
				player.getPlayfield().updateWithData(fullCmd.getFieldData());
				fGameInstance.setTurn(fullCmd.isMyTurn() ? fGameInstance.getMe() : fGameInstance.getEnemy());
				updatePlayFields();
				
				break;
				
			case PartialUpdate:
				PartialUpdateCommand partialCmd = (PartialUpdateCommand)command;
				Player p = partialCmd.isMyField() ? fGameInstance.getMe() : fGameInstance.getEnemy();
				p.getPlayfield().updatePosition(partialCmd.getPosX(), partialCmd.getPosY(), partialCmd.getNewValue());
				fGameInstance.setTurn(partialCmd.isMyTurn() ? fGameInstance.getMe() : fGameInstance.getEnemy());
				updatePlayFields();
				break;
				
			case Win:
				WinCommand winCmd = (WinCommand)command;
				PlayerSettings settings = PlayerSettings.getInstance(null);
				if (winCmd.isWin()) {
					settings.setWins(settings.getWins()+1);
					settings.save();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(getBaseContext(), "Congratulations. You won against " + fGameInstance.getEnemy().getName(), Toast.LENGTH_LONG).show();
						}
					});
				} else {
					settings.setLoses(settings.getLoses()+1);
					settings.save();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(getBaseContext(), fGameInstance.getEnemy().getName() + " crushed your forces with ease!", Toast.LENGTH_LONG).show();	
						}
					});
					
				}
			
				fTCPClient.disconnect();
				
				Intent intent = new Intent(GameActivity.this, MainActivity.class);
			    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
			    startActivity(intent);
				
				break;
				
			default:
				break;
		}
	}

}

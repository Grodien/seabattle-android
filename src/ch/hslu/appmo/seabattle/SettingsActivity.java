package ch.hslu.appmo.seabattle;

import ch.hslu.appmo.seabattle.models.PlayerSettings;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	private TextView flblWins;
	private TextView flblLoses;
	private EditText ftxtName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		flblWins = (TextView) findViewById(R.id.lblWins);
		flblLoses = (TextView) findViewById(R.id.lblLoses);
		ftxtName = (EditText) findViewById(R.id.txtName);
		
		PlayerSettings settings = PlayerSettings.getInstance(this);
		
		flblWins.setText(String.valueOf(settings.getWins()));
		flblLoses.setText(String.valueOf(settings.getLoses()));
		ftxtName.setText(settings.getPlayerName());
	}
	
	@Override
	public void onBackPressed() {
		saveSettings();
		super.onBackPressed();
	}
	
	@Override
	protected void onPause() {
		saveSettings();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		saveSettings();
		super.onDestroy();
	}
	
	private void saveSettings() {
		PlayerSettings instance = PlayerSettings.getInstance(this);
		instance.setPlayerName(ftxtName.getText().toString());
		instance.save();
	}
}

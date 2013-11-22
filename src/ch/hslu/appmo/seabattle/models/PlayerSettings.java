package ch.hslu.appmo.seabattle.models;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class PlayerSettings {
	
	private static PlayerSettings instance = null;
	
	public static synchronized PlayerSettings getInstance(Activity activity) {
		if (instance == null) {
			instance = new PlayerSettings(activity);
		}
		return instance;
	}
	
	private String fPlayerName;
	private int fWins;
	private int fLoses;
	private Activity fActivity;
	
	private PlayerSettings(Activity activity) {
		fActivity = activity;
		load();		
	}

	public boolean hasSettings() {
		return fPlayerName != null;
	}
	
	public void load() {
		SharedPreferences sharedPreferences = fActivity.getPreferences(Activity.MODE_PRIVATE);
		fPlayerName = sharedPreferences.getString("name", null);
		fWins = sharedPreferences.getInt("wins", 0);
		fLoses = sharedPreferences.getInt("loses", 0);
	}
	
	public void save() {
		SharedPreferences sharedPreferences = fActivity.getPreferences(Activity.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString("name", fPlayerName);
		editor.putInt("wins", fWins);
		editor.putInt("loses", fLoses);
		editor.commit();
	}	
	
	public String getPlayerName() {
		return fPlayerName;
	}

	public void setPlayerName(String playerName) {
		fPlayerName = playerName;
	}

	public int getWins() {
		return fWins;
	}

	public void setWins(int wins) {
		fWins = wins;
	}

	public int getLoses() {
		return fLoses;
	}

	public void setLoses(int loses) {
		fLoses = loses;
	}
}

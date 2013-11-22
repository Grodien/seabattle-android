package ch.hslu.appmo.seabattle.models;

public class Player {
	private String fName;
	private PlayField fPlayfield;
	private boolean fReady;
	
	public Player(String name) {
		fName = name;
		fPlayfield = new PlayField();
		fReady = false;
	}
	
	public String getName() {
		return fName;
	}
	public void setName(String name) {
		fName = name;
	}
	public PlayField getPlayfield() {
		return fPlayfield;
	}
	public void setPlayfield(PlayField playfield) {
		fPlayfield = playfield;
	}

	public boolean isReady() {
		return fReady;
	}

	public void setReady(boolean ready) {
		fReady = ready;
	}

}

package ch.hslu.appmo.seabattle.models;


public class Game {

	private static Game instance = null;
	
	public static synchronized Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}
		return instance;
	}
	
	private Player fMe;
	private Player fEnemy;
	private Player fTurn;
	
	private Game() {}
	
	public Game newGame(Player me, Player enemy) {
		fMe = me;
		fEnemy = enemy;
		fTurn = enemy;
		return this;
	}

	public Player getMe() {
		return fMe;
	}

	public void setMe(Player me) {
		fMe = me;
	}

	public Player getEnemy() {
		return fEnemy;
	}

	public void setEnemy(Player enemy) {
		fEnemy = enemy;
	}

	public Player getTurn() {
		return fTurn;
	}

	public void setTurn(Player turn) {
		fTurn = turn;
	}
}

package ch.hslu.appmo.seabattle.models;

public class PlayField {
	public static final int VALUE_FREE = 0;
	public static final int VALUE_SHIP = 1;
	public static final int VALUE_FREE_HIT = 2;
	public static final int VALUE_SHIP_HIT = 3;
	
	private int[][] fFieldData;
	
	public PlayField() {
		fFieldData = new int[GameSettings.SIZE][GameSettings.SIZE];
		
		for (int i = 0; i < GameSettings.SIZE; i++) {
			for (int j = 0; j < GameSettings.SIZE; j++) {
				fFieldData[i][j] = VALUE_FREE;
			}
		}
	}
	
	public PlayField(String data) {
		fFieldData = new int[GameSettings.SIZE][GameSettings.SIZE];
		
		updateWithData(data);
	}
	
	public int getFieldData(int column, int row) {
		return fFieldData[column][row];
	}
	
	public void updateWithData(String data) {
		for (int i = 0; i < GameSettings.SIZE; i++) {
			for (int j = 0; j < GameSettings.SIZE; j++) {
				fFieldData[i][j] = Integer.parseInt(data.substring(i*GameSettings.SIZE+j, i*GameSettings.SIZE+j+1));
			}
		}
	}
	
	public void updatePosition(int column, int row, int value) {
		fFieldData[column][row] = value;
	}
}

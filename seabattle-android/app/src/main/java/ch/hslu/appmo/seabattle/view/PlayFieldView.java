package ch.hslu.appmo.seabattle.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import ch.hslu.appmo.seabattle.models.GameSettings;
import ch.hslu.appmo.seabattle.models.PlayField;
import ch.hslu.appmo.seabattle.models.Player;

public class PlayFieldView extends View {

	private Player fPlayer;
	private int fSize;
	private Paint fColorBlack;
	private Paint fColorRed;
	private Paint fColorBlue;
	
	private Rect fRect;
	private boolean fHideShips;
	private Paint fColorWhite;

	public PlayFieldView(Context context, int size, Player player, boolean hideShips) {
		super(context);
		fSize = size;
		fPlayer = player;
		fHideShips = hideShips;
		
		fColorBlack = new Paint();
		fColorBlack.setColor(Color.BLACK);
		fColorBlack.setStrokeWidth(2);
		fColorRed = new Paint();
		fColorRed.setColor(Color.RED);
		fColorRed.setStrokeWidth(0);
		fColorBlue = new Paint();
		fColorBlue.setColor(Color.BLUE);
		fColorBlue.setStrokeWidth(0);
		fColorWhite = new Paint();
		fColorWhite.setColor(Color.WHITE);
		fColorWhite.setStrokeWidth(0);
		
		fRect = new Rect();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(fSize, fSize);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		PlayField playfield = fPlayer.getPlayfield();
		
		int fieldSize = fSize / GameSettings.SIZE;
		
		for (int j = 0; j < GameSettings.SIZE; j++) {
			for (int i = 0; i < GameSettings.SIZE; i++) {
				switch (playfield.getFieldData(i, j)) {
					case PlayField.VALUE_FREE:
						fColorBlack.setStrokeWidth(2);
						fRect.set(i*fieldSize, j*fieldSize, (i+1)*(fieldSize+1), (j+1)*fieldSize);
						canvas.drawRect(fRect, fColorBlack);
						fRect.set(i*fieldSize+2, j*fieldSize+2, (i+1)*(fieldSize)-2, (j+1)*fieldSize-2);
						canvas.drawRect(fRect, fColorWhite);
						break;
					case PlayField.VALUE_FREE_HIT:
						fColorBlack.setStrokeWidth(2);
						fRect.set(i*fieldSize, j*fieldSize, (i+1)*(fieldSize+1), (j+1)*fieldSize);
						canvas.drawRect(fRect, fColorBlack);
						fRect.set(i*fieldSize+2, j*fieldSize+2, (i+1)*(fieldSize)-2, (j+1)*fieldSize-2);
						canvas.drawRect(fRect, fColorBlue);
						break;
					case PlayField.VALUE_SHIP:
						fColorBlack.setStrokeWidth(2);
						fRect.set(i*fieldSize, j*fieldSize, (i+1)*(fieldSize+1), (j+1)*fieldSize);
						canvas.drawRect(fRect, fColorBlack);
						if (!fHideShips) {
							fColorBlack.setStrokeWidth(0);
							fRect.set(i*fieldSize+2, j*fieldSize+2, (i+1)*(fieldSize)-2, (j+1)*fieldSize-2);
							canvas.drawRect(fRect, fColorBlack);
						}
						break;
					case PlayField.VALUE_SHIP_HIT:
						fColorBlack.setStrokeWidth(2);
						fRect.set(i*fieldSize, j*fieldSize, (i+1)*(fieldSize+1), (j+1)*fieldSize);
						canvas.drawRect(fRect, fColorBlack);
						fRect.set(i*fieldSize+2, j*fieldSize+2, (i+1)*(fieldSize)-2, (j+1)*fieldSize-2);
						canvas.drawRect(fRect, fColorRed);
						break;
				}			
			}
		}
	}
}

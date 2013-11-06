package ch.hslu.appmo.seabattle.command.player;

import java.util.ArrayList;

public class PlayerShootCommand extends PlayerCommand {

	private int fPosX;
	private int fPosY;

	public PlayerShootCommand(int posX, int posY) {
		super(PlayerCommandType.PlayerShoot);
		fPosX = posX;
		fPosY = posY;
	}

	@Override
	public ArrayList<Object> getParams() {
		ArrayList<Object> list = new ArrayList<Object>();
		list.add(fPosX);
		list.add(fPosY);
		
		return list;
	}

	public int getPosX() {
		return fPosX;
	}

	public void setPosX(int posX) {
		fPosX = posX;
	}

	public int getPosY() {
		return fPosY;
	}

	public void setPosY(int posY) {
		fPosY = posY;
	}
}

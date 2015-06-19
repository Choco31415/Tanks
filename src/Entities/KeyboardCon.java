package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Main.GamePanel;

/*
 * This tank is controlled by the keyboard.
 */

public class KeyboardCon extends Controller {
	
	private Tank tank;
	private Color color = new Color(0.6f, 0.6f, 0.1f);
	private int minesDestroyed = 0;
	
	private int[] ArrowKeys = new int[]{KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_LEFT};
	private int[] WASD = new int[]{KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_A};
	private int[] keys;
	
	GamePanel gp;
	
	/*
	 * boolean ArrowKeys:
	 * True: The controller will use the arrow keys.
	 * False: The controller will use WASD.
	 */
	public KeyboardCon(GamePanel gp_, Tank tank_, boolean arrowKeys) {
		gp = gp_;
		tank = tank_;
		tank.setController(this);
		if (arrowKeys) {
			keys = ArrowKeys;
		} else {
			keys = WASD;
		}
	}
	
	public void update(ArrayList<Integer> keyPressed) {
		double[] input = new double[2];
		if (keyPressed.contains(keys[0])) {
			input[0] = 2;
		}
		
		if (keyPressed.contains(keys[1])) {
			input[1] = 1;
		} else if (keyPressed.contains(keys[2])) {
			input[1] = -1;
		}
		tank.update(input);
		
	}
	
	public void draw(Graphics2D g) {
		tank.draw(g, color);
	}

	@Override
	public ArrayList<Mine> getMines() {
		return gp.mines;
	}

	@Override
	public void mineDestroyed() {
		minesDestroyed++;
		gp.mineDestroyed();
		
	}
	
	public int getMinesDestroyed() {
		return minesDestroyed;
	}
	
	public void updateClosestMine() {
		tank.findClosestMine();
	}
	
	public Tank getTank() {
		return tank;
	}
	
	@Override
	public String toString() {
		return "ComRandomConTank: " + minesDestroyed + " mines destroyed";
	}
}

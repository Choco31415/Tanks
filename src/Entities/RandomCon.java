package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import Main.GamePanel;

/*
 * This tank is semi-random. It moves in semi-circles.
 * Its name is short for "Random Controller", which is a misnomer.
 */

public class RandomCon extends Controller {

	/*
	 * This tank goes in semi-circles. Is slightly better than completely random (ComRandomConTank).
	 */
	
	private Tank tank;
	private Color color = new Color(0.3f, 0.6f, 0.0f);
	private int minesDestroyed = 0;
	
	int i = 0;
	
	GamePanel gp;
	
	public RandomCon(GamePanel gp_, Tank tank_) {
		gp = gp_;
		tank = tank_;
		tank.setController(this);
	}
	
	@Override
	public void update(ArrayList<Integer> keyPressed) {
		i++;
		double[] input = new double[2];
		input[0] = 2;
		if (i%180 < 90) {
			input[1] = 0.05;
		} else {
			input[1] = -0.05;
		}
		tank.update(input);
	}

	@Override
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

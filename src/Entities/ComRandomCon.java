package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

import Main.GamePanel;

/*
 * This tank is completely random.
 * Its name is short for "Completely Random Controlled"
 */

public class ComRandomCon extends Controller {
	
	private Tank tank;
	private Color color = new Color(0.0f, 0.6f, 0.3f);
	private int minesDestroyed = 0;
	
	Random rng = new Random();
	
	GamePanel gp;
	
	public ComRandomCon(GamePanel gp_, Tank tank_) {
		gp = gp_;
		tank = tank_;
		tank.setController(this);
	}
	
	@Override
	public void update(ArrayList<Integer> keyPressed) {
		double[] input = new double[2];
		input[0] = rng.nextInt(3);
		input[1] = rng.nextDouble()/10.0-0.05;
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

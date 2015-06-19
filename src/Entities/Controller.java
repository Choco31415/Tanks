package Entities;

import java.awt.Graphics2D;
import java.util.ArrayList;

/*
 * Tank is the class representing a tank.
 * Controller is the class that tells a tank what to do.
 */

public abstract class Controller {
	
	public abstract ArrayList<Mine> getMines();
	public abstract void mineDestroyed();
	public abstract int getMinesDestroyed();
	public abstract Tank getTank();
	public abstract void update(ArrayList<Integer> keyPressed);
	public abstract void draw(Graphics2D g);
	public abstract void updateClosestMine();
	public abstract String toString();
}

package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import Main.GamePanel;

public class Mine {
	
	// location and size
	private int x;
	private int y;
	private final int size = 4;
	
	// random
	private Random rng = new Random();
	
	public Mine(int x_, int y_) {
		x = x_;
		y = y_;
	}
	
	public Mine() {
		x = rng.nextInt(GamePanel.WIDTH);
		y = rng.nextInt(GamePanel.HEIGHT);
	}
	
	public void draw(Graphics2D g) {
		//Draw the mine!!!
		g.setColor(Color.RED);
		g.drawRect((int)(x - size/2),(int)(y - size/2), size, size);
		g.fillRect((int)(x - size/2),(int)(y - size/2), size, size);
		
	}

	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
}

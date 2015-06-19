package Entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import Main.GamePanel;

/*
 * Tank is the class representing a tank.
 * Controller is the class that tells a tank what to do.
 */

public class Tank {
	
	// location, rotation, size
	public double x;
	public double y;
	public double r;
	public final static int size = 12;
	
	// mobility restrictions
	public final static float motionDampening = 0.8f;
	public final static int rotationDampening = 15;
	public final static int ceilInput = 1;
	
	// use intractability
	private ArrayList<Integer> keyPressed = new ArrayList<Integer>();
	
	// tank body
	private ArrayList<RotaPoly> body = new ArrayList<RotaPoly>();
	
	// for screen size
	Controller con;
	
	// for testing
	private int minI;
	
	// for drawing
	private Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
	private Stroke plain = new BasicStroke();
	
	public Tank(double x_, double y_, double r_) {
		
		/* set position */
		x = x_;
		y = y_;
		
		/* body */
		int[] tempX = new int[]{(0 - size/2), (0 - size/2), size/2, size/2};
		int[] tempY = new int[]{(0 - size/2), size/2, size/2, (0 - size/2)};
		body.add(new RotaPoly(this, tempX, tempY));
		
		/* wheels */
		tempX = new int[]{(0 - size/2), (0 - size/2), (0 - (3*size)/4), (0 - (3*size)/4)};
		tempY = new int[]{(0 - (3*size)/4), (0 + (3*size)/4), (0 + (3*size)/4), (0 - (3*size)/4)};
		body.add(new RotaPoly(this, tempX, tempY));
		tempX = new int[]{size/2, size/2, (3*size)/4, (3*size)/4};
		tempY = new int[]{(0 - (3*size)/4), (3*size)/4, (3*size)/4, (0 - (3*size)/4)};
		body.add(new RotaPoly(this, tempX, tempY));
		
		/* snout */
		tempX = new int[]{size/8, size/8, (0 - size/8), (0 - size/8)};
		tempY = new int[]{size/2, size, size, size/2};
		body.add(new RotaPoly(this, tempX, tempY));
		
	}
	
	public void setController(Controller con_) {
		/* send in the pointers, where are the pointers, we ought to have pointers */
		con = con_;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getRot() {
		return r;
	}

	public void draw(Graphics2D g, Color color) {
		
		int dx = 0;
		int dy = 0;
		
		/* line to nearest mine */
		if (minI != -1 && GamePanel.lineToMine) {
	        g.setStroke(dashed);
			g.setColor(Color.BLUE);
			try {
				g.draw(new Line2D.Double(x, y, con.getMines().get(minI).getX(), con.getMines().get(minI).getY()));
			} catch (IndexOutOfBoundsException e) {}
			g.setStroke(plain);
		}
		
		/* tank */
		g.setColor(color);
		drawBody(0, 0, g, color);
		
		/* redraw tank if near or over edge */
		if (x - 2.5*size < 0) {
			drawBody(GamePanel.WIDTH, 0, g, color);
			dx = GamePanel.WIDTH;
		} else if (x + 2.5*size > GamePanel.WIDTH) {
			drawBody(-GamePanel.WIDTH, 0, g, color);
			dx = -GamePanel.WIDTH;
		}
		
		if (y - 2.5*size < 0) {
			drawBody(0, GamePanel.HEIGHT, g, color);
			dy = GamePanel.HEIGHT;
		} else if (y + 2.5*size > GamePanel.HEIGHT) {
			drawBody(0, -GamePanel.HEIGHT, g, color);
			dy = -GamePanel.HEIGHT;
		}
		
		if (dx != 0 && dy != 0) {
			drawBody(dx, dy, g, color);
		}
	}
	
	private void drawBody(double d, double dy, Graphics2D g, Color color) {
		x += d;
		y += dy;
		for (int i = 0; i < body.size(); i++) {
			body.get(i).draw(g, color);
		}
		x -= d;
		y -= dy;
	}

	public void update(double[] input) {
		// Restricting tank movements to reasonable values.
		if (input[0] < ceilInput) {
			if (input[0] < -ceilInput) {
				input[0] = -1f;
			} else {
				input[0] = input[0]/ceilInput;
			}
		} else {
			input[0] = 1f;
		}
		input[0] = input[0]/motionDampening;
		x += input[0]*(-Math.sin(r));
		y += input[0]*(Math.cos(r));
		
		//Restricting tank rotation to reasonable values.
		if (input[1] < ceilInput) {
			if (input[1] < -ceilInput) {
				input[1] = -1f;
			} else {
				input[1] = input[1]/ceilInput;
			}
		} else {
			input[1] = 1f;
		}
		r += input[1]/rotationDampening;
		
		x = x%GamePanel.WIDTH;
		y = y%GamePanel.HEIGHT;
		
		/* Java mod fails. Some code to fix that. */
		if (x<0) {
			x += GamePanel.WIDTH;
		}
		if (y<0) {
			y += GamePanel.HEIGHT;
		}
		
		checkForMines();
	}
	
	private void checkForMines() {
		
		// find closest mine
		double minD;
		minI = -1;
		minD = findClosestMine();
		
		// remove closest mine
		if (minI != -1) {
			if (minD < 1.5*size) {
				//con.getMines().remove(minI);
				con.getMines().set(minI, new Mine());
				con.mineDestroyed();
				findClosestMine();
			}
		}
	}
	
	public void updateClosestMine() {
		findClosestMine();
	}
	
	public double findClosestMine() {
		double distance;
		double minD = 9000;
		minI = -1;
		for (int i = 0; i < con.getMines().size(); i++) {
			distance = Math.sqrt(Math.pow(x - con.getMines().get(i).getX(), 2) + Math.pow(y - con.getMines().get(i).getY(), 2));
			if (distance < minD) {
				minD = distance;
				minI = i;
			}
		}
		return minD;
	}
	
	public double[] getAngleVector() {
		return new double[]{-Math.sin(r), Math.cos(r)};
	}
	
	public double[] getClosestMineVector() {
		return new double[]{con.getMines().get(minI).getX() - x, con.getMines().get(minI).getY() - y};
	}
	
	public void keyPressed(int keyCode) {
		if (!keyPressed.contains(keyCode)) {
			keyPressed.add(keyCode);
		}
	}
	
	public void keyReleased(int keyCode) {
		keyPressed.remove((Object) keyCode);
	}
}

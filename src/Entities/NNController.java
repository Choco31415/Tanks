package Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import neuralNetwork.NeuralNetwork;
import Main.GamePanel;

/*
 * This tank is controlled by a Neural Network.
 */

public class NNController extends Controller {
	
	private Tank tank;
	private Color color = new Color(0.0f, 0.8f, 0.0f);
	private int minesDestroyed = 0;
	NeuralNetwork brains = new NeuralNetwork(4, 2, 2, 6);
	
	GamePanel gp;
	
	public NNController(GamePanel gp_, Tank tank_) {
		gp = gp_;
		tank = tank_;
		tank.setController(this);
	}
	
	public void update(ArrayList<Integer> keysPressed) {
		/*
		 * Input array key:
		 * Slot 0: x coordinate of the direction vector
		 * Slot 1: y coordinate of the direction vector
		 * Slot 2: x coordinate of the unit vector pointing towards the closest mine
		 * Slot 3: y coordinate of the unit vector pointing towards the closest mine
		 * 
		 * The input array is what we feed into the tank's NN.
		 */
		double[] input = new double[4];
		double[] vector = tank.getAngleVector();
		input[0] = vector[0];
		input[1] = vector[1];
		vector = tank.getClosestMineVector();
		double distance = Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
		vector[0] = vector[0]/distance;
		vector[1] = vector[1]/distance;
		input[2] = vector[0];
		input[3] = vector[1];
		
		/*
		 * We pass information through the NN and get back what the tank should do.
		 * Because the NN passes back values in the 0.0 to 1.0 range, we alter the output values to range from -1.0 to 1.0.
		 */
		vector = brains.update(input);
		vector[0] = vector[0]*2-1;
		vector[1] = vector[1]*2-1;
		
		//We move the tank.
		tank.update(vector);
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
	
	public NeuralNetwork getNN() {
		return brains;
	}
	
	public Tank getTank() {
		return tank;
	}
	
	public void clearMinesDestroyed() {
		minesDestroyed = 0;
	}
	
	@Override
	public String toString() {
		return "ComRandomConTank: " + minesDestroyed + " mines destroyed";
	}
}

package Main;

import geneticAlgorithm.GController;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Entities.RandomCon;
import Entities.Controller;
import Entities.KeyboardCon;
import Entities.Mine;
import Entities.ComRandomCon;
import Entities.Tank;

/**
 * 
 * This class is basically the "main" class of the code, handling the genetic algorithm, tanks, mines, ect...
 * 
 * A lot of aspects of the program I left as variables so that they could be edited easily and quickly.
 * Feel free to edit them!
 *
 */

@SuppressWarnings({ "serial", "unused" })
public class GamePanel extends JPanel implements Runnable, KeyListener {
	
	// window dimensions
	public static final int WIDTH = 900;
	public static final int HEIGHT = 600;
	public static final int SCALE = 1;
	public static boolean showWindow; // Tells if the window is showing or not.
	
	// game thread variables
	private Thread thread;
	private boolean running;
	private int speed; // How many multiples of 60 is the FPS?
	private final int fastFPSmultiplier = 25; // If the window is not showing (aka fast mode), what multiple of normalFPS should the FPS be?
	private final int windowFPSmultiplier = 1; // If the window is showing, what multiple of normalFPS should the FPS be?
	private final int normalFPS = 60;
	private int FPS; // Frames per second. Easy peasy lemon squeezy.
	private float targetTime; // The time each game loop should take in milliseconds.
	
	// image variables
	private BufferedImage image;
	private Graphics2D g;
	
	// entities, yay!
	public ArrayList<Controller> tanks = new ArrayList<Controller>();
	public ArrayList<Controller> NNtanks = new ArrayList<Controller>(); // tanks controlled by the genetic algorithm, or the variable "con"
	
	// mine, mine, mine, you greedy person
	public ArrayList<Mine> mines = new ArrayList<Mine>();
	public static final int mineCount = 40;
	
	// stats
	private int minesDestroyed = 0;
	
	// customization ;; true = draw a blue line between a tank and its closest mine
	public static boolean lineToMine = true;
	
	// key presses
	private ArrayList<Integer> keysPressed = new ArrayList<Integer>();
	
	// generation information
	public final static int lifeTime = 60; //seconds
	GController con = new GController(this);
	
	// graphical speed information
	long elapsed; // time between each frame draw
	long avgElapsed = (long)(0); // resitant measure of time elapsed between each frame draw
	
	// print some simulation information ;; only works if run from a Java development environment
	boolean extraInfo = false;
	int updateFrequency = 2; // Updates come every (updateFrequency) seconds. Can be quite finicky.
	
	// JFrame pointer
	JFrame game;
	
	public GamePanel(boolean showWindow_, JFrame game_) {
		super();
		showWindow = showWindow_;
		if (showWindow) {
			setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
			setFPS(windowFPSmultiplier*normalFPS);
		} else {
			setPreferredSize(new Dimension(80, 50));
			setFPS(fastFPSmultiplier*normalFPS);
		}
		setFocusable(true); // Allows focusing of input to specific JPanel item.
		requestFocus();
		game = game_;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#addNotify()
	 */
	public void addNotify() {
		// Connects COMPONENT to native screen source.
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	private void init() {
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		
		/* Enable anti-aliasing */
	    //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		/* Create player tank */
		if (showWindow) {
			tanks.add(new KeyboardCon(this, new Tank(450.0, 300.0, 0.0), true));
		}
		
		for (int i = 0; i < mineCount; i ++) {
			mines.add(new Mine());
		}
		
		/* We've started our program */
		running = true;
		
	}
	
	public void run() {
		
		init();
		
		long start;
		//long elapsed
		long wait;
		float totalTime;
		long currentTime;
		float updateTime = 0f;
		
		//game loop
		totalTime = 0;
		while(running) {
			//1000 is a second.
			totalTime += targetTime;

			start = System.nanoTime();
			
			//Do the brunt of things.
			update();
			updateClosestMine();
			draw();
			drawToScreen();
			
			//Pushing a new generation
			if (totalTime > lifeTime*1000/speed) {
				con.pushNewGeneration();
				minesDestroyed = 0;
				totalTime = 0;
				if (con.getIntelligenceAlreadyFound()) {
					
				}
			}
			
			//Updates, if enabled
			updateTime += targetTime;
			if (extraInfo && updateTime > updateFrequency*1000) {
				System.out.println("Avg. Run. Overhead: " + avgElapsed/1000000.0);
				System.out.println("Avg. Max FPS: " + (1000.0) / (avgElapsed/1000000.0));
				System.out.println("Target FPS: " + FPS);
				System.out.println("cGen: " + con.cGen);
				System.out.println("Progress to next Gen: " + (int)(totalTime * speed / 10 / lifeTime) + "%");
				System.out.println("Average fitness for generation " + (con.cGen-1) + ": " + con.getAverageFitness());
				updateTime = 0;
			}
			
			//Update times, and wait if needed.
			currentTime = System.nanoTime();
			
			elapsed = currentTime - start;
			
			// Calculate average running overhead for 1 second
			avgElapsed = (avgElapsed * 29 + elapsed) / 30;
			
			wait = (long) (targetTime - elapsed / 1000000.0); 
			
			if (wait > 0.0) {
				try {
					Thread.sleep(wait);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/*
	 * This method takes tanks through one frame in the program.
	 */
	private void update() {
		for (int i = 0; i < NNtanks.size(); i++) {
			NNtanks.get(i).update(keysPressed);
		}
		for (int i = 0; i < tanks.size(); i++) {
			tanks.get(i).update(keysPressed);
		}
	}
	
	/*
	 * This method forces tanks to recalculate the closest mine to them.
	 * This is unfortunately necessary.
	 */
	private void updateClosestMine() {
		for (int i = 0; i < NNtanks.size(); i++) {
			NNtanks.get(i).updateClosestMine();
		}
	}
	
	/*
	 * This method handles all graphical information, but does not draw to the screen.
	 */
	private void draw() {
		if (showWindow) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, WIDTH, HEIGHT);
	
			/* Draw mines */
			for (int i = 0; i < mines.size(); i++) {
				mines.get(i).draw(g);
			}
			
			/* Draw tanks */
			for (int i = 0; i < NNtanks.size(); i++) {
				NNtanks.get(i).draw(g);
			}
			for (int i = 0; i < tanks.size(); i++) {
				tanks.get(i).draw(g);
			}
			
			/* Sort tanks based on performance */
			int[] toSort = new int[NNtanks.size()];
			for (int i = 0; i < NNtanks.size(); i++) {
				toSort[i] = i;
			}
			bubbleSort(toSort);
			
			/* Mark the best tank with yellow circle. */
			g.setColor(new Color(0.8f, 0.8f, 0.4f));
			int width = Tank.size/2 + 2;
			g.fillOval((int)NNtanks.get(toSort[toSort.length-1]).getTank().x - width/2, (int)NNtanks.get(toSort[toSort.length-1]).getTank().y - width/2, width, width);
			
			/* Mark the next 40% best tanks with a green circle. */
			g.setColor(new Color(0.2f, 0.7f, 0.3f));
			for (int i = toSort.length-2; i >= toSort.length*0.6; i--) {
				if (NNtanks.get(toSort[i]).getMinesDestroyed() != 0) {
					g.fillOval((int)NNtanks.get(toSort[i]).getTank().x - width/2, (int)NNtanks.get(toSort[i]).getTank().y - width/2, width, width);
				}
			}
			
			/* Finish things up. */
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 100, 10);
			g.fillRect(0, 10, 190, 10);
			g.fillRect(0, 20, 40, 10);
			g.fillRect(0, 30, 80, 10);
			g.fillRect(0, 40, 110, 10);
			g.fillRect(0, 50, 80, 10);
			g.fillRect(0, HEIGHT-10, 280, 10);
			g.setColor(Color.BLACK);
			Font font = g.getFont();
			g.setFont(font.deriveFont(10.0f));
			g.drawString("Mines Destroyed: " + minesDestroyed, 0, 9);
			g.drawString("Last Generation Average Fitness: " + (Math.round(con.getAverageFitness()*100.0)/100.0), 0, 19);
			g.drawString("cGen: " + con.cGen, 0, 29);
			g.drawString("Overhead: " + (int)(elapsed / 1000000), 0, 39);
			g.drawString("Run. Avg. Overhead: " + (Math.round(avgElapsed/10000.0)/100.0), 0, 49);
			g.drawString("Target Time: " + targetTime, 0, 59);
			g.drawString("Press q to toggle between window mode and 'fast mode'.", 0, HEIGHT-1);
		}
	}
	
	/*
	 * This method pushes all graphical information to the screen. Aka it draws everything.
	 */
	private void drawToScreen() {
		if (showWindow) {
			Graphics g2 = getGraphics();
			g2.drawImage(image,  0,  0, WIDTH * SCALE, HEIGHT * SCALE, null);
			g2.dispose();
		}
	}
	
	/*
	 * This method keeps track of the global count of mines destroyed.
	 */
	public void mineDestroyed() {
		minesDestroyed++;
	}

	public void keyTyped(KeyEvent key) {}
	public void keyPressed(KeyEvent key) {
		if (!keysPressed.contains(key.getKeyCode())) {
			keysPressed.add(key.getKeyCode());
		}
		
		//Option for switching between window and fast mode.
		if (key.getKeyCode() == KeyEvent.VK_Q) {
			toggleWindow();
		}
	}
	
	public void keyReleased(KeyEvent key) {
		keysPressed.remove((Object) key.getKeyCode());
	}
	
	/*
	 * Used by the program to figure out which tanks are performing the best.
	 * Not the best sorting algorithm, but it's simple and it works.
	 */
    public void bubbleSort(int array[]) {
        int n = array.length;
        int k;
        for (int m = n; m >= 0; m--) {
            for (int i = 0; i < n - 1; i++) {
                k = i + 1;
                if (NNtanks.get(array[i]).getMinesDestroyed() > NNtanks.get(array[k]).getMinesDestroyed()) {
                    swapNumbers(i, k, array);
                }
            }
        }
    }
  
    /*
     * Used to swap two array items.
     */
    private void swapNumbers(int i, int j, int[] array) {
 
        int temp;
        temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    /*
     * Used to toggle the window between real time and run-everything-as-fast-as-possible-mode.
     */
    public void toggleWindow() {
    	showWindow = !showWindow;
    	if (showWindow) {
    		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
    		setFPS(windowFPSmultiplier*normalFPS);
    	} else {
    		setPreferredSize(new Dimension(80, 50));
    		setFPS(fastFPSmultiplier*normalFPS);
    	}
    	game.pack();
    }
    
    /*
     * Used to appropriately set the FPS.
     */
    private void setFPS(int target) {
    	FPS = target;
    	targetTime = 1000f / FPS;
    	speed = target / 60;
    }
    
	/*
	 * Notifies the gamepanel to avoid loss of data.
	 */
	public void close() {
		con.close();
	}
}

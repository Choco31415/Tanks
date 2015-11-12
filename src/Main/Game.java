package Main;

import javax.swing.JFrame;

/**
 * 
 * @author ErnieParke
 * 
 * This is the "root" class that creates the program from the ground up, but it's very uninteresting.
 * 
 */

@SuppressWarnings({ "serial", "unused" })
public class Game {

	public static final boolean showWindow = true;
	public static JFrame window;
	
	public static void main(String[] args) {
		
		//A JFrame is a window with decorations such as a border, title, and button components.
		
		window = new JFrame("Tanks AI Test");
		final GamePanel gamePane = new GamePanel(showWindow, window);
		window.setContentPane(gamePane);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
		window.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) { gamePane.close(); }
		});
	}
	
	/*
	 * This method is used by gamepanel whenever the window needs to be resized.
	 */
	public void pack() {
		window.pack();
	}
}

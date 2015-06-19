package geneticAlgorithm;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import Entities.Controller;
import Entities.NNController;
import Entities.Tank;
import Main.GamePanel;

/**
 * A genetic algorithm originally learned from: http://www.ai-junkie.com/ga/intro/gat1.html
 * 
 * GController is a short hand way to say Genetic Algorithm Controller. Probably not the best name.
 * 
 * This class creates a series of N tanks controlled by neural networks.
 * The neural networks are breed by a genetic algorithm.
 * Each generation normally lasts 60 seconds, though the length of each generation can be changed in the GamePanel class.
 * 
 * For the unfamiliar, a genetic algorithm is basically code trying to mimic evolution.
 * The best individuals in a generation have the highest chance of breeding into the next generation.
 * Or, the individuals with the highest fitness in a generation have the highest chance of breeding into the next generation.
 * Fitness here is determined by the number of mines a tank picks up in a generation. 
 */

public class GController {
	
	final int N = 30; //How many members are there per generation? Cannot be odd.
	final double goal = 10; //The goal mines per tank efficiency.
	final float crossoverRate = 0.7f; //How often two randomly selected tanks will splice with each other.
	final float mutationRate = 0.05f; //How often a bit in a tank will mutate. Higher rates require higher N.
	final double MaxPerturbation = 0.3;
	final int hugeNumber = 99999; //Used in a few not very important places.
	final boolean writerOn = true; //This variable controls if the genetic algorithm is being recorded or not. Can generate large files for huge search depths and huge N!
	final int suppressLowest = 3; //Automatically removes the n weakest from the current generation. 0 to turn off. If set too high, can cause Nan fitnessScores.
	
	public int cGen = 0; //Current Generation
	
	/* The next five variables are used for tracking the best performances overall and in each generation. */
	float averageFitness;
	Float maximumFit = (float) 0;
	Controller maxFitIndividual = null;
	Float localFit = (float) 0;
	Controller localFitIndividual = null;
	
	private Random rnd = new Random(); //The rng. Not sure why I called it rnd.
	PrintWriter writer = null; //The writer, which keeps track of important information in a .txt file if used.
	
	Controller[] tanks = new Controller[N]; //Stores an array of pointers to tanks.
    
	GamePanel gp; //The gamepanel, so that we can call methods in it.
	
	boolean intelligenceAlreadyFound = false; //Used to keep track of if we've encountered interesting behavior or not.
	
	public GController(GamePanel gp_) {
		gp = gp_;
		init();
	}
	
	public void init() {		
		//Generate our beginning population.
		Controller temp;
		gp.NNtanks.clear();
		for (int i = 0; i < N; i++) {
			temp = new NNController(gp, new Tank(rnd.nextInt(GamePanel.WIDTH), rnd.nextInt(GamePanel.HEIGHT), 0.0));
			
			//Because Java passes in pointers, we can be assured that tanks and NNtanks now point to the same tank.
			tanks[i] = temp;
			gp.NNtanks.add(temp);
		}
		
		//Get the writer generated.
		if (writerOn) {
			try {
				writer = new PrintWriter("test.txt", "UTF-8");
			} catch (FileNotFoundException e) {
				System.out.println("Err");
			} catch (UnsupportedEncodingException e) {
				System.out.println("Err");				
			}
			writer.println("The goal mines per tank efficiency is: " + goal);
			writer.println("Per generation, the lowest " + suppressLowest + " are suppressed.");
			writer.println("Each generation takes " + GamePanel.lifeTime + " second(s) to run.\n");
		}
	}
	
	/*
	 * This method creates a new generation of tanks depending on how well each tank has done.
	 * This method uses a genetic algorithm to produce the next generation of tanks.
	 */
	@SuppressWarnings("unused")
	public void pushNewGeneration() {
		//Increment the current generation.
		cGen++;
		
		//Temporary variables for later.
		double[] tank1Data;
		double[] tank2Data;
		int splice;
		double[] tank1NewData;
		double[] tank2NewData;

		Float[] fitnessScores = new Float[N];
		RouletteWheel rw;
		
		localFit = 0f;
		
		//We test each individual's fitness. This is measured by the number of mines picked up in a minute.
		for (int i = 0; i < N; i++) {
			try {
				fitnessScores[i] = (float) tanks[i].getMinesDestroyed();
			} catch(NullPointerException e) {
				if (tanks[i] == null) {
					System.out.println("The tank was null.");
				}
			}
			
			//We check to see if the tank is the best performing in its generation.
			if (fitnessScores[i] > localFit) {
				localFit = fitnessScores[i];
				localFitIndividual = tanks[i];
			}
		}
		
		//We figure out if the best tank this generation is the best performing tank generated so far.
		if (localFit > maximumFit) {
			maximumFit = localFit;
			maxFitIndividual = localFitIndividual;
		}
		
		//We record the current generation of individuals.
		if (writerOn) {
			writer.println("Generation: " + cGen);
			/*for (int i = 0; i < N; i++) {
				writer.println(interpret(tanks[i]));
			}*/
		}
		
		//We calculate the average fitness of the tanks.
		averageFitness = 0;
		for (int i = 0; i < fitnessScores.length; i++) {
			averageFitness += fitnessScores[i];
		}
		averageFitness /= N;
		
		//Record the average fitness.
		if (writerOn) {
			writer.println("Average Mines Destroyed Per Tank: " + averageFitness);
		}
		
		//Check if interesting behaviors have developed.
		if (averageFitness < goal) {
			//Interesting behaviors were not achieved. Create a new generation and retry.
			//First we put all the tanks into a rhoulette wheel.
			rw = new RouletteWheel(tanks, fitnessScores);
			
			if (suppressLowest > 0) {
				rw.removeLowest(suppressLowest);
			}
			
			try {
				rw.normalizeWeights(20f);
			} catch (InvalidWeights e) {
				System.out.println("You messed up.");
			}
			
			//Here we start creating the next generation of tanks, sporting plasma shields and party cannons.
			for (int i = 0; i < N; i += 2) {
				//Accessing tank NN data.
				tank1Data = ((NNController) rw.pickRandomItem()).getNN().getWeights();
				tank2Data = ((NNController) rw.pickRandomItem()).getNN().getWeights();
				
				tank1NewData = new double[tank1Data.length];
				tank2NewData = new double[tank1Data.length];
				
				//Crossing over will occur randomly.
				if (rnd.nextDouble() < crossoverRate) {
					//Crossover will occur.
					splice = rnd.nextInt(tank1Data.length);
					for (int j = 0; j < splice; j++) {
						tank1NewData[j] = tank1Data[j]; 
					}
					for (int j = splice; j < tank2Data.length; j++) {
						tank1NewData[j] = tank2Data[j]; 
					}
					for (int j = 0; j < splice; j++) {
						tank2NewData[j] = tank2Data[j]; 
					}
					for (int j = splice; j < tank1Data.length; j++) {
						tank2NewData[j] = tank1Data[j]; 
					}
				} else {
					//Crossover will not occur.
					tank1NewData = tank1Data;
					tank2NewData = tank2Data;
				}
				
				//Mutate the two tanks, as in change some random NN values to slightly different values.
				for (int j = 0; j < tank1Data.length; j++) {
					if (rnd.nextDouble() < mutationRate) {
						tank1NewData[j] += (rnd.nextDouble() - 0.5)*MaxPerturbation*2;
						if (tank1NewData[j] > 1) {
							tank1NewData[j] = 1f;
						}
						if (tank1NewData[j] < -1) {
							tank1NewData[j] = -1f;
						}
					}
				}
				for (int j = 0; j < tank1Data.length; j++) {
					if (rnd.nextDouble() < mutationRate) {
						tank2NewData[j] += (rnd.nextDouble() - 0.5)*MaxPerturbation*2;
						if (tank2NewData[j] > 1) {
							tank2NewData[j] = 1f;
						}
						if (tank2NewData[j] < -1) {
							tank2NewData[j] = -1f;
						}
					}
				}
				
				//Push the changes.
				((NNController) tanks[i]).getNN().putWeights(tank1NewData);
				((NNController) tanks[i+1]).getNN().putWeights(tank2NewData);
			}
			
			//Record our roulette wheel.
			if (writerOn) {
				writer.println("Rw: " + rw);
				writer.println("Total Mines Destroyed: " + averageFitness*N);
				writer.println("Local maximum value achieved: " + localFit);
				writer.println("It's interpretation is: " + interpret(localFitIndividual) + "\n");
			}
			
			//Our new generation is complete.
		} else {
			//Houston, we have found intelligence.
			if (!intelligenceAlreadyFound) {
				if (writerOn) {
					writer.println("\nInteresting behaviors have been reached.");
					
					writer.println("Global maximum value achieved: " + maximumFit);
					writer.println("It is interpreted as: " + interpret(maxFitIndividual));
					writer.close();
				}
			

				System.out.println("\nFinal Generation: " + cGen);
				System.out.println("Global maximum value achieved: " + maximumFit);
				System.out.println("It is interpreted as: " + interpret(maxFitIndividual));
				
				/*tanks = null;
				close();
				System.exit(0);*/

				gp.toggleWindow();
			}
			intelligenceAlreadyFound = true;
		}
		
		//Reset mine destroyed counts.
		for (int i = 0; i < N; i++) {
			((NNController) tanks[i]).clearMinesDestroyed();
		}
		
	}
	
	/*
	 * This method gets the average fitness of the most recently finished generation.
	 * Returns 0 if no generations have been finished.
	 */
	public float getAverageFitness() {
		return averageFitness;
	}
	
	/*
	 * Used by the GamePanel for testing purposes only. Currently not used.
	 */
	public boolean getIntelligenceAlreadyFound() {
		return intelligenceAlreadyFound;
	}
	
	/*
	 * This method is for convenience/testing purposes only.
	 * This method turns a Tank into a string representation. Currently not used.
	 */
	public String interpret(Controller tank) {
		return null;
	}

	/*
	 * This method is called by the gamepanel so that the writer can be closed.
	 * This preserves all of the text the writer has written so far.
	 */
	public void close() {
		if (writerOn) {
			writer.println("The program has terminated at generation " + cGen + ".");
			writer.println("Global maximum value achieved: " + maximumFit);
			writer.println("It is interpreted as: " + interpret(maxFitIndividual));
			writer.close();
		}
	}
}

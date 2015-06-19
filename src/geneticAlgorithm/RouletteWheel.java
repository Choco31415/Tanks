package geneticAlgorithm;

import java.util.Arrays;
import java.util.Random;

/*
 * A roulette wheel is a circular object.
 * This circular object is cut into several radial slices.
 * Each radial slice varies in size, and each slice has an object associated with it.
 * Picking an object on the roulette wheel is done by spinning the wheel and seeing what object corresponds to the slice that lands pointing upwards.
 * 
 * This class tries to mimic a real roulette wheel. It can take any class as an input type.
 */

public class RouletteWheel {
	final int size; //How many items are in the wheel?
	Object[] items; //What items are in the wheel?
	Float[] weights; //What are the weights of items in the wheel?
	int[] pickCounts; //How many times have items in the wheel been picked?
	Float weightSize = null; //Combined total of weights.
	
	private Random rnd = new Random();
	
	public RouletteWheel(int size_) {
		size = size_;
		items = new Object[size];
		weights = new Float[size];
		initPickCounts();
	}
	
	public RouletteWheel(Object[] items_) {
		size = items_.length;
		items = items_;
		weights = new Float[size];
		initPickCounts();
	}
	
	public RouletteWheel(Object[] items_, Float[] weights_) {
		size = items_.length;
		items = items_;
		weights = weights_;
		initPickCounts();
		try {
			sumWeights();
		} catch (InvalidWeights e) {
			System.out.println("You messed up.");
		}
	}
	
	public void initPickCounts() {
		//Initializes pickCounts. See above for what pickCounts is.
		pickCounts = new int[size];
		for (int i = 0; i < size; i++) {
			pickCounts[i] = 0;
		}
	}
	
	public void setWeight(Float weight, int i) {
		//This function sets the weight at i to weight.
		weights[i] = weight;
	}
	
	public void sumWeights() throws InvalidWeights {
		//This function finds the sum of wheel weights.
		Float sum = 0.0f;
		for (int i = 0; i < size; i++) {
			if (weights[i].equals(null)) {
				throw new InvalidWeights();
			} else {
				sum += weights[i];
			}
		}
		weightSize = sum;
	}
	
	public void normalizeWeights(Float target) throws InvalidWeights {
		//This function normalizes weights so that their total is the same as specified in the function.
		sumWeights();
		Float factor = target/weightSize;
		for (int i = 0; i < size; i++) {
			weights[i] *= factor;
		}
		weightSize = target;
	}
	
	public void forcePositiveWeights() throws InvalidWeights {
		//This method is good for making all weights positive, if not done already.
		for (int i = 0; i < size; i++) {
			if (weights[i].equals(null)) {
				throw new InvalidWeights();
			} else {
				weights[i] = Math.abs(weights[i]);
			}
		}
	}
	
	public Object pickRandomItem() {
		//This function picks a random item, based randomly on an item's weight in relation to other items' weight.
		try {
			sumWeights();
		} catch (InvalidWeights e) { }
		Float i = rnd.nextFloat()*weightSize;
		Float j = 0.0f;
		int k;
		for (k = 0; j < i; k++) {
			try {
				j += weights[k];
			} catch (ArrayIndexOutOfBoundsException e) {
				//Normally an error shouldn't be thrown, but occassionally it does happen...
				return items[items.length-1];
			}
		}
		try {
			pickCounts[k-1]++;
			return items[k-1];
		} catch (ArrayIndexOutOfBoundsException e) {
			//Extremely rare error. Only occurs once every 400,000 search depths with N being 20.
			pickCounts[0]++;
			return items[0];
		}
	}
	
	public void removeLowest(int n) {
		if (n > size-1 || n == 0) {
			return;
		}
		
		//We hunt for the lowest values.
		Float[] lowest = new Float[n];
		Arrays.fill(lowest, Float.MAX_VALUE);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < n; j++) {
				if (weights[i] < lowest[j]) {
					lowest[j] = weights[i];
					j = n;
				}
			}
		}
		
		//One the lowest values are calculated, we act!
		Float newWeight;
		for (int i = 0; i < size; i++) {
			newWeight = weights[i] - lowest[n-1];
			if (newWeight>0) {
				weights[i] = newWeight;
			} else {
				weights[i] = 0f;
			}
		}
	}
	
	@Override
	public String toString() {
		if (weightSize == null) {
			return "No weightsize detected.";
		}
		String foo = "Rhoulette Wheel of size " + size + ":\n";
		for (int i = 0; i < size; i++) {
			foo += "Item: " + items[i] + " (Weight " + weights[i] + " // Picked " + pickCounts[i] + " time(s))\n";
		}
		return foo;
	}
}

package neuralNetwork;

import java.util.Random;

public class Neuron {
	// The number of inputs into the neuron.
	int numInputs;
	
	// The weights for each input.
	double[] weights;
	
	public Neuron(int numInputs_) {
		
		Random rnd = new Random();
		
		numInputs = numInputs_;
		weights = new double[numInputs+1];
		
		for (int i = 0; i < numInputs+1; i++) {
			
			//Set up weights with a random value.
			
			weights[i] = rnd.nextDouble()*2.0-1.0;
		}
	}
}

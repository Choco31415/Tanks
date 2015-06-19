package neuralNetwork;

/*
 * This is the neural network (NN) class.
 * The way that the NN class is organized is that the NN class stores several layers of neurons.
 * Each layer of neurons stores neurons.
 * Neurons are the building block/class of the neural network.
 * 
 * Based off of the C version at: http://www.ai-junkie.com/ann/evolved/nnt1.html
 */

public class NeuralNetwork {
	int numInputs; // The number of input neurons.
	int numOutputs; // The number of output neurons.
	int numHiddenLayers; // The number of layers in between the input and output layers + 1.
	int neuronsPerHiddenLyr; // Number of neurons per hidden layer.
	
	NeuronLayer[] neuronLayers;
	
	public NeuralNetwork(int numInputs_, int numOutputs_, int numHiddenLayers_, int neuronsPerHiddenLyr_) {
		numInputs = numInputs_;
		numOutputs = numOutputs_;
		numHiddenLayers = numHiddenLayers_;
		neuronsPerHiddenLyr = neuronsPerHiddenLyr_;
		
		neuronLayers = new NeuronLayer[numHiddenLayers+1];
		
		createNet();
	}
	
	/*
	 * This method creates our NN.
	 */
	public void createNet() {
		// Create the neural network layers!!!
		if (numHiddenLayers > 0) {
			// Create the layers of the network.
			neuronLayers[0] = new NeuronLayer(neuronsPerHiddenLyr, numInputs);
			
			//Create output layer.
			for (int i = 0; i < numHiddenLayers-1; i++) {
				neuronLayers[i + 1] = new NeuronLayer(neuronsPerHiddenLyr, neuronsPerHiddenLyr);
			}
			
			// Create output layer.
			neuronLayers[neuronLayers.length-1] = new NeuronLayer(numOutputs, neuronsPerHiddenLyr);
		} else {
			// Create output layer.
			neuronLayers[0] = new NeuronLayer(numOutputs, numInputs);
		}
	}
	
	/*
	 * This function returns an ArrayList of the weights used in the NN.
	 * Note: An ArrayList is used, instead of an array, for ease of use and simplicity.
	 */
	public double[] getWeights() {
		// This will hold the weights.
		double[] weights = new double[getNumberOfWeights()];
		
		int cWeight = 0;
		
		// For each layer.
		for (int i = 0; i < numHiddenLayers + 1; i++) {
			
			// For each neuron.
			for (int j = 0; j < neuronLayers[i].numNeurons; j++) {
				
				// For each weight.
				for (int k = 0; k < neuronLayers[i].neurons[j].numInputs; k++) {
					weights[cWeight++] = neuronLayers[i].neurons[j].weights[k];
				}
			}
		}
		
		return weights;
	}
	
	/*
	 * This function gets the number of weights needed in a NN.
	 */
	public int getNumberOfWeights() {
		int weights = 0;
		
		// For each layer.
		for (int i = 0; i < numHiddenLayers + 1; i++) {
			
			// For each neuron.
			for (int j = 0; j < neuronLayers[i].numNeurons; j++) {
				
				// For each weight.
				for (int k = 0; k < neuronLayers[i].neurons[j].numInputs; k++) {
					weights++;
				}
			}
		}
		
		return weights;
	}
	
	/*
	 * This function replaces the weights in a NN with the given weights.
	 */
	public void putWeights(double[] weights) {
		int cWeight = 0;
		
		// For each layer.
		for (int i = 0; i < numHiddenLayers + 1; i++) {
			
			// For each neuron.
			for (int j = 0; j < neuronLayers[i].numNeurons; j++) {
				
				// For each weight.
				for (int k = 0; k < neuronLayers[i].neurons[j].numInputs; k++) {
					neuronLayers[i].neurons[j].weights[k] = weights[cWeight++];
				}
			}
		}
	}
	
	/*
	 * This function will run the inputs through the NN and obtain an output.
	 */
	public double[] update(double[] inputs) {
		// Stores the resultant outputs from each layer
		double[] outputs = null;
		double netinput;
		
		int cWeight = 0;
		
		// Check that we have the correct number of inputs.
		
		if (inputs.length != numInputs) {
			// Output nothing if incorrect.
			
			return null;
		}
		
		// Go through each layer...
		//System.out.println("New");
		for (int i = 0; i < numHiddenLayers + 1; i++) {
			if (i > 0) {
				inputs = outputs;
			}
			
			outputs = new double[neuronLayers[i].numNeurons];
			
			// For each neuron sum the (inputs * corresponding weights).
			// Return the total at our sigmoid function to get the ouput.
			
			//System.out.println("Layer");
			for (int j = 0; j< neuronLayers[i].numNeurons; j++) {
				
				netinput = 0;
				
				cWeight = 0;
				
				int numInputs = neuronLayers[i].neurons[j].numInputs;
				
				// For each weight
				
				for (int k = 0; k < numInputs ; k++) {
					// Sum the weights x inputs
					netinput += neuronLayers[i].neurons[j].weights[k] * inputs[cWeight++];
					//System.out.println(k + ":" + netinput);
				}
				
				// Add in bias
				netinput -= neuronLayers[i].neurons[j].weights[numInputs-1]; 
				outputs[j] = sigmoid(netinput, 1.0);
			}
		}
		
		return outputs;
	}
	
	public double sigmoid(double netinput, double response) {
		return ( 1 / ( 1 + Math.exp(-netinput / response)));
	}
}

package neuralNetwork;

public class NeuronLayer {
	
	int numNeurons;
	
	Neuron[] neurons;
	
	public NeuronLayer(int numNeurons_, int numInputsPerNeuron) {
		
		numNeurons = numNeurons_;
		neurons = new Neuron[numNeurons]; 
		
		for (int i = 0; i < numNeurons; i++) {
			neurons[i] = new Neuron(numInputsPerNeuron);
		}
	}
}

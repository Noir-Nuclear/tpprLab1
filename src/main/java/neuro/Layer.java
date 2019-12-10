package neuro;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Layer {
    List<Perceptron> perceptrons;

    public Layer(int size, int percerptronSize, double beta) {
        perceptrons = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            perceptrons.add(new Perceptron(beta, percerptronSize));
        }
    }

    List<Double> calculateOutput(List<Double> inputs) {
        return perceptrons.stream().map(p -> p.calculateOutput(inputs)).collect(Collectors.toList());
    }

    List<Double> getOutputs() {
        return perceptrons.stream().map(p -> p.output).collect(Collectors.toList());
    }
}

package neuro;

import math.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Perceptron {
    private List<Double> weights;
    private Double beta;
    public Double output;

    public Perceptron(Double beta, int size) {
        this.beta = beta;
        weights = new ArrayList<>();
        IntStream.range(0, size + 1).forEach(i -> weights.add(Math.random() * 7 - 3.5));
    }

    public List<Double> getWeights() {
        return weights;
    }

    public void setWeights(List<Double> weights) {
        this.weights = weights;
    }

    Double getWeight(int i) {
        return weights.get(i);
    }

    Double calculateOutput(List<Double> inputs) {
        List<Double> currentInputs = new ArrayList<>(inputs);
        currentInputs.add(1.0);
        output = MathUtils.activateF(IntStream.range(0, currentInputs.size()).mapToDouble(i -> weights.get(i) * currentInputs.get(i)).sum(), beta);
        return output;
    }
}

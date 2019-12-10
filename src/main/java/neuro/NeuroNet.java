package neuro;

import java.util.ArrayList;
import java.util.List;

public class NeuroNet {
    private List<Layer> layers;

    public NeuroNet(List<Integer> configuration, double beta) {
        layers = new ArrayList<>();
        for (int i = 1; i < configuration.size(); i++) {
            layers.add(new Layer(configuration.get(i), configuration.get(i - 1), beta));
        }
    }

    public Double getOutput(List<Double> inputs) {
        List<Double> currentInputs = new ArrayList<>(inputs);
        layers.forEach(layer -> {
            List<Double> buf = layer.calculateOutput(currentInputs);
            currentInputs.clear();
            currentInputs.addAll(buf);
        });
        return currentInputs.get(0);
    }

    public List<Layer> getLayers() {
        return layers;
    }
}

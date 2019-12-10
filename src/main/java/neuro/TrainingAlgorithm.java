package neuro;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class TrainingAlgorithm {
    NeuroNet neuroNet;
    Double speed;
    Double beta;
    private Double output;
    List<List<Double>> sigmas;

    public TrainingAlgorithm(List<Integer> configutaion, Double beta, Double speed) {
        sigmas = new ArrayList<>();
        neuroNet = new NeuroNet(configutaion, beta);
        this.speed = speed;
        this.beta = beta;
    }

    public Double getOutput(List<Double> row) {
        return neuroNet.getOutput(row.subList(1, row.size()));
    }

    public void train(List<Double> row) {
        output = neuroNet.getOutput(row.subList(1, row.size()));
        findSigmas(row.get(0));
        correctWeights(row.subList(1, row.size()));
    }

    public void findSigmas(Double y) {
        sigmas.clear();
        sigmas.add(List.of(
                (y - output) * beta * y * (1 - y))
        );
//        sigmas.add(List.of(
//                (output - y) * beta * output * (1 - output))
//        );


        for (int i = neuroNet.getLayers().size() - 2; i >= 0; i--) {
            List<Double> layerSigmas = new ArrayList<>();
            Layer currentLayer = neuroNet.getLayers().get(i);
            int finalI = i;
            IntStream.range(0, currentLayer.perceptrons.size()).forEach(j -> {
                layerSigmas.add(
                        beta * currentLayer.perceptrons.get(j).output *
                                (1 - currentLayer.perceptrons.get(j).output) *
                                IntStream.range(0, sigmas.get(0).size()).
                                        mapToDouble(k ->
                                                sigmas.get(0).get(k) *
                                                        neuroNet.getLayers().get(finalI + 1).perceptrons.get(k).getWeight(j)
                                        ).sum()
                );
            });
            sigmas.add(0, layerSigmas);
        }
    }

    public void correctWeights(List<Double> row) {
        List<Double> outputs = new ArrayList<>(row);
        outputs.add(1.0);
        for (int i = 0; i < neuroNet.getLayers().size(); i++) {
            for (int j = 0; j < neuroNet.getLayers().get(i).perceptrons.size(); j++) {
                List<Double> weights = neuroNet.getLayers().get(i).perceptrons.get(j).getWeights();
                List<Double> newWeights = new ArrayList<>();
                for (int k = 0; k < weights.size(); k++) {
                    newWeights.add(weights.get(k) + speed * (
                            sigmas.get(i).get(j) * outputs.get(k))
                    );
                }
                neuroNet.getLayers().get(i).perceptrons.get(j).setWeights(newWeights);
            }
            outputs = new ArrayList<>(neuroNet.getLayers().get(i).getOutputs());
            outputs.add(1.0);
        }
    }
}

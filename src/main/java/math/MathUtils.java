package math;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class MathUtils {
    public static List<List<Double>> normalizeData(List<List<Double>> data) {
        List<List<Double>> result = new ArrayList<>();
        IntStream.range(0, data.get(0).size()).forEach(j -> {
            Double max = data.stream().max(Comparator.comparing(row -> row.get(j))).get().get(j);
            Double min = data.stream().min(Comparator.comparing(row -> row.get(j))).get().get(j);
            IntStream.range(0, data.size()).forEach(i -> {
                if (result.size() <= i) {
                    ArrayList<Double> newRow = new ArrayList<>();
                    newRow.add((data.get(i).get(j) - min) / (max - min));
                    result.add(newRow);
                } else {
                    result.get(i).add((data.get(i).get(j) - min) / (max - min));
                }
            });
        });
        return result;
    }

    public static double activateF(double x, double beta) {
        return (1 / (1 + Math.exp(-beta * x)));
    }
}

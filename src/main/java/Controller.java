import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import loading.TXTLoader;
import math.MathUtils;
import neuro.TrainingAlgorithm;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    public ScatterChart chart;
    public ScheduledFuture task;
    public Button begin;
    public NumberAxis xAxis;
    public NumberAxis yAxis;
    public TextField configurationField;
    public TextField betaField;
    public TextField percent;
    public TextField speed;
    public TextField teachError;
    public TextField testError;
    public Button stop;
    public TextField iterationField;
    List<List<Double>> rows;
    Set<Integer> testingData;
    Set<Integer> approvedData;
    List<Integer> conf;
    TrainingAlgorithm algorithm;
    Random random;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TXTLoader loader = new TXTLoader("D:\\MyFolder\\Projects\\Java\\tpprLab1\\src\\main\\resources\\txt\\data.txt");
        int n = loader.loadNumberOfColumns();
        int countOfRows = loader.loadNumberOfRows();
        chart.setAnimated(false);
        rows = MathUtils.normalizeData(loader.loadRows());
        conf = new ArrayList<>();
        conf.add(n - 1);
        conf.add(1);
        testingData = new HashSet<>();
        approvedData = new HashSet<>();
        random = new Random();
        begin.setOnAction(event -> {
                    iterationField.setText("");
                    if (percent.getText().isEmpty() ||
                            configurationField.getText().isEmpty() ||
                            betaField.getText().isEmpty() ||
                            speed.getText().isEmpty()) {
                        return;
                    }
                    while (testingData.size() < countOfRows * Double.parseDouble(percent.getText())) {
                        testingData.add(random.nextInt(countOfRows - 1));
                    }
                    for (int i = 0; i < countOfRows; i++) {
                        if (!testingData.contains(i)) {
                            approvedData.add(i);
                        }
                    }
                    while (conf.size() != 2) {
                        conf.remove(1);
                    }
                    conf.addAll(1,
                            Arrays.stream(configurationField.getText().split(";")).
                                    map(Integer::parseInt).collect(Collectors.toList())
                    );
                    algorithm = new TrainingAlgorithm(conf, Double.parseDouble(betaField.getText()), Double.parseDouble(speed.getText()));
                    ScheduledExecutorService scheduledExecutorService;
                    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

                    // put dummy data onto graph per second
                    task = scheduledExecutorService.scheduleAtFixedRate(() -> {

                        // Update the chart
                        Platform.runLater(this::renderChart);
                    }, 0, 60, TimeUnit.MILLISECONDS);
                }
        );
        stop.setOnAction(event -> {
            if (task != null) {
                task.cancel(false);
                task = null;
            }
        });
    }

    private void renderChart() {
        chart.getData().clear();
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        List<Integer> indices = new ArrayList<>();
        testingData.forEach(i -> algorithm.train(rows.get(i)));
        IntStream.range(0, testingData.size()).forEach(i -> indices.add(0, getNext()));
        series1.setName("Обучающая выборка");
        AtomicReference<Double> teach = new AtomicReference<>(0.0);
        indices.forEach(i -> {
            series1.getData().add(
                    new XYChart.Data(rows.get(i).get(0), algorithm.getOutput(rows.get(i))));
            teach.updateAndGet(v -> v + Math.pow(rows.get(i).get(0) - algorithm.getOutput(rows.get(i)), 2.0));
        });
        teach.updateAndGet(v -> v / 2);
        teachError.setText(teach.get().toString());
        AtomicReference<Double> test = new AtomicReference<>(0.0);
        approvedData.forEach(i -> {
            series2.getData().add(
                    new XYChart.Data(rows.get(i).get(0), algorithm.getOutput(rows.get(i))));
            test.updateAndGet(v -> v + Math.pow(rows.get(i).get(0) - algorithm.getOutput(rows.get(i)), 2.0));
        });
        test.updateAndGet(v -> v / 2);
        testError.setText(test.get().toString());
        series2.setName("Проверочная выборка");
        chart.getData().addAll(series1, series2);
        iterationField.setText(String.valueOf(iterationField.getText().length() > 0 ? Integer.parseInt(iterationField.getText()) + 1 : 0));
    }

    public Integer getNext() {
        Integer index;
        do {
            index = random.nextInt(rows.size());
        } while (!testingData.contains(index));
        return index;
    }

    public Double transform(Double value) {
        return Math.log(1 / value - 1) / (-1.0);
    }
}

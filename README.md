# jfxchart

### example

    DualXYChart<String, Double, Double> dualXYChart =
        new DualXYChart(new CategoryAxis(), new NumberAxis(), new NumberAxis(), LineChart.class, AreaChart.class);
    dualXYChart.getPrimaryData().add(series1);
    dualXYChart.getSecondaryData().add(series2);

![DualXYChart Screenshot](https://github.com/bhs324/jfxchart/tree/master/src/main/doc/DualXYChart.png)
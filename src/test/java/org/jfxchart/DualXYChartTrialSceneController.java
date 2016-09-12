/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jfxchart;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author bhs
 */
public class DualXYChartTrialSceneController implements Initializable {

    private static int buttonCounter = 0;
    private XYChart.Series series1;
    private XYChart.Series series2;

    @FXML
    private AnchorPane chartArea;
    @FXML
    private ToggleButton primaryToggle;
    @FXML
    private ToggleButton secondaryToggle;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        series1 = generateSeries1();
        series2 = generateSeries2();

        DualXYChart<String, Double, Double> dualXYChart = new DualXYChart(new CategoryAxis(), new NumberAxis(), new NumberAxis(),
                LineChart.class, AreaChart.class);
        dualXYChart.getPrimaryData().add(series1);
        dualXYChart.getSecondaryData().add(series2);

        AnchorPane.setTopAnchor(dualXYChart, 0d);
        AnchorPane.setRightAnchor(dualXYChart, 0d);
        AnchorPane.setBottomAnchor(dualXYChart, 0d);
        AnchorPane.setLeftAnchor(dualXYChart, 0d);

        chartArea.getChildren().add(dualXYChart);

        dualXYChart.getPrimaryChart().visibleProperty().bind(primaryToggle.selectedProperty());
        dualXYChart.getSecondaryChart().visibleProperty().bind(secondaryToggle.selectedProperty());
    }

    private XYChart.Series generateSeries1() {
        XYChart.Series series1 = new XYChart.Series();
        series1.setName("series1");
        series1.getData().add(new XYChart.Data("a", 23));
        series1.getData().add(new XYChart.Data("b", 14));
        series1.getData().add(new XYChart.Data("c", null));
        series1.getData().add(new XYChart.Data("d", null));
        series1.getData().add(new XYChart.Data("e", 15));
        return series1;
    }

    private XYChart.Series generateSeries2() {
        XYChart.Series series2 = new XYChart.Series();
        series2.setName("series2");
        series2.getData().add(new XYChart.Data("a", null));
        series2.getData().add(new XYChart.Data("b", 33));
        series2.getData().add(new XYChart.Data("c", 24));
        series2.getData().add(new XYChart.Data("d", 25));
        series2.getData().add(new XYChart.Data("e", null));
        return series2;
    }

    @FXML
    void generateValue(ActionEvent event) {
        String category = String.valueOf((char) ('f' + buttonCounter));
        series1.getData().add(new XYChart.Data<>(category, buttonCounter % 3 == 0 ? Math.random() * 100 : null));
        series2.getData().add(new XYChart.Data<>(category, buttonCounter % 3 == 0 ? Math.random() * 100 : null));
        buttonCounter++;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jfxchart;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @param <X>
 * @param <Y1>
 * @param <Y2>
 * @author bhs
 */
// TODO: legend 위치, 시리즈 색상 통합, 시리즈에 누락된 값 있어도 차트 모양 안깨지도록.
public class DualXYChart<X, Y1 extends Number, Y2 extends Number> extends StackPane {

    public static final Logger logger = LoggerFactory.getLogger(DualXYChart.class);

    private XYChart<X, Y1> primaryChart;
    private XYChart<X, Y2> secondaryChart;

    public DualXYChart(Axis<X> xAxis, Axis<Y1> yAxis1, Axis<Y2> yAxis2,
                       Class<? extends XYChart<X, Y1>> primaryClass, Class<? extends XYChart<X, Y2>> secondaryClass) {
        super();

        try {
            Constructor<? extends XYChart<X, Y1>> primaryCon = primaryClass.getConstructor(Axis.class, Axis.class);
            Constructor<? extends XYChart<X, Y2>> secondaryCon = secondaryClass.getConstructor(Axis.class, Axis.class);
            setPrimaryChart(primaryCon.newInstance(xAxis, yAxis1));
            setSecondaryChart(secondaryCon.newInstance(xAxis, yAxis2));

            resizeChart();

            super.getChildren().addAll(getPrimaryChart(), getSecondaryChart());
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException("Could not find constructor(Axis<X>, Axis<Y>)", ex);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("Could not instantiate chart class", ex);
        }
    }

    @Override
    public ObservableList<Node> getChildren() {
        ObservableList<Node> list = FXCollections.observableArrayList(primaryChart, secondaryChart);
        return FXCollections.unmodifiableObservableList(list);
    }

    public final XYChart<X, Y1> getPrimaryChart() {
        return primaryChart;
    }

    public final void setPrimaryChart(XYChart<X, Y1> chart) {
        primaryChart = chart;
    }

    public final XYChart<X, Y2> getSecondaryChart() {
        return secondaryChart;
    }

    public final void setSecondaryChart(XYChart<X, Y2> chart) {
        secondaryChart = chart;
    }

    public final ObservableList<XYChart.Series<X, Y1>> getPrimaryData() {
        return new XAxisCheckedSeriesList(new NullCheckedSeriesList(primaryChart.getData()), secondaryChart.getData());
    }

    public final ObservableList<XYChart.Series<X, Y2>> getSecondaryData() {
        return new XAxisCheckedSeriesList(new NullCheckedSeriesList(secondaryChart.getData()), primaryChart.getData());
    }

    private static class XAxisCheckedSeriesList<X, Y extends Number> extends ModifiableObservableListBase<Series<X, Y>> {

        private final ObservableList<XYChart.Series<X, Y>> mySeriesList;
        private final ObservableList<XYChart.Series<X, Y>> otherSeriesList;

        public XAxisCheckedSeriesList(ObservableList<XYChart.Series<X, Y>> myList, ObservableList<XYChart.Series<X, Y>> otherList) {
            mySeriesList = myList;
            otherSeriesList = otherList;
            mySeriesList.addListener((ListChangeListener.Change<? extends Series<X, Y>> c) -> {
                // TODO 코드정리
                if (c.next()) {
                    // 현재 차트에 추가된 x값이 다른 차트에 없을 경우 다른 차트에 (x, null)값을 추가함.
                    for (Series<X, Y> mySeries : c.getAddedSubList()) {
                        for (X addedX : mySeries.getData().stream().map(Data::getXValue).collect(Collectors.toList())) {
                            for (Series<X, Y> otherSeries : otherSeriesList) {
                                if (!otherSeries.getData().stream().map(Data::getXValue).collect(Collectors.toList()).contains(addedX)) {
                                    otherSeries.getData().add(new Data<>(addedX, null));
                                    // TODO 정렬순서 설정 가능하도록
                                    otherSeries.getData().sort((o1, o2) -> o1.getXValue().toString().compareTo(o2.getXValue().toString()));
                                    JFXCharts.checkNullData(otherSeries);
                                }
                            }
                        }
                    }
                    // 다른 차트에 있는 x값이 현재 차트에 없을 경우 현재 차트에 (x, null)값을 추가함.
                    for (Series<X, Y> otherSeries : otherList) {
                        for (X otherX : otherSeries.getData().stream().map(Data::getXValue).collect(Collectors.toList())) {
                            for (Series<X, Y> mySeries2 : c.getAddedSubList()) {
                                if (!mySeries2.getData().stream().map(Data::getXValue).collect(Collectors.toList()).contains(otherX)) {
                                    mySeries2.getData().add(new Data<>(otherX, null));
                                    // TODO 정렬순서 설정 가능하도록
                                    mySeries2.getData().sort((o1, o2) -> o1.getXValue().toString().compareTo(o2.getXValue().toString()));
                                    JFXCharts.checkNullData(mySeries2);
                                }
                            }
                        }
                    }
                }
            });
        }

        @Override
        public Series<X, Y> get(int index) {
            return mySeriesList.get(index);
        }

        @Override
        public int size() {
            return mySeriesList.size();
        }

        @Override
        protected void doAdd(int index, Series<X, Y> element) {
            mySeriesList.add(element);
        }

        @Override
        protected Series<X, Y> doSet(int index, Series<X, Y> element) {
            return mySeriesList.set(index, element);
        }

        @Override
        protected Series<X, Y> doRemove(int index) {
            return mySeriesList.remove(index);
        }
    }

    private static class NullCheckedSeriesList<X, Y extends Number> extends ModifiableObservableListBase<XYChart.Series<X, Y>> {

        private final List<Series<X, Y>> seriesList;

        public NullCheckedSeriesList(List<XYChart.Series<X, Y>> list) {
            seriesList = list;
            list.forEach(JFXCharts::checkNullData);
        }

        @Override
        public XYChart.Series<X, Y> get(int index) {
            return seriesList.get(index);
        }

        @Override
        public int size() {
            return seriesList.size();
        }

        @Override
        protected void doAdd(int index, XYChart.Series<X, Y> element) {
            // TODO 시리즈의 각 데이터가 변경될때마다도 널 체크 하도록.
//        element.getData().addListener((ListChangeListener.Change<? extends XYChart.Data<X, Y>> c) -> {
//        });
            JFXCharts.checkNullData(element);
            seriesList.add(index, element);
        }

        @Override
        protected XYChart.Series<X, Y> doSet(int index, XYChart.Series<X, Y> element) {
            // TODO 시리즈의 각 데이터가 변경될때마다도 널 체크 하도록.
//        element.getData().addListener((ListChangeListener.Change<? extends XYChart.Data<X, Y>> c) -> {
//        });
            JFXCharts.checkNullData(element);
            return seriesList.set(index, element);
        }

        @Override
        protected XYChart.Series<X, Y> doRemove(int index) {
            return seriesList.remove(index);
        }
    }

    private static final double Y_AXIS_WIDTH = 30;

    private void resizeChart() {
        primaryChart.getYAxis().setSide(Side.LEFT);
        primaryChart.getYAxis().setMinWidth(Y_AXIS_WIDTH);
        primaryChart.getYAxis().setPrefWidth(Y_AXIS_WIDTH);
        primaryChart.getYAxis().setMaxWidth(Y_AXIS_WIDTH);
        StackPane.setAlignment(primaryChart, Pos.CENTER_LEFT);
        primaryChart.getStylesheets().add(this.getClass().getResource(this.getClass().getSimpleName() + ".css").toExternalForm());

        secondaryChart.getYAxis().setSide(Side.RIGHT);
        secondaryChart.getYAxis().setMinWidth(Y_AXIS_WIDTH);
        secondaryChart.getYAxis().setPrefWidth(Y_AXIS_WIDTH);
        secondaryChart.getYAxis().setMaxWidth(Y_AXIS_WIDTH);
        StackPane.setAlignment(secondaryChart, Pos.CENTER_RIGHT);
        secondaryChart.getStylesheets().add(this.getClass().getResource(this.getClass().getSimpleName() + ".css").toExternalForm());

        StackPane.setMargin(primaryChart, new Insets(0, Y_AXIS_WIDTH, 0, 0));
        StackPane.setMargin(secondaryChart, new Insets(0, 0, 0, Y_AXIS_WIDTH));
    }
}

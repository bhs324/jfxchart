package org.jfxchart;

import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

/**
 * Created by bhs on 2016-09-12.
 */
public class JFXCharts {

    /**
     * @param series
     * @param <X>
     * @param <Y>    시리즈 데이터의 Y값에 null을 사용 가능하도록 함.
     *               null값은 심볼없이 직선으로 채움.
     */
    public static <X, Y extends Number> void checkNullData(XYChart.Series<X, Y> series) {
        Integer lastNotNullIndex = null;
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<X, Y> currentData = series.getData().get(i);
            // null값을 중간값으로 바꾸기 때문에 null값과 실제값을 구분할 수 없다. extraValue를 보고 실제값을 확인한다.
            currentData.setExtraValue(currentData.getYValue());

            if (currentData.getExtraValue() != null) {

                if (lastNotNullIndex != null) {
                    fillNullData(series, lastNotNullIndex + 1, i);
                } else {
                    fillNullData(series, 0, i, currentData.getExtraValue());
                }

                lastNotNullIndex = i;
            } else if (i == series.getData().size() - 1 && lastNotNullIndex != null) {
                fillNullData(series, lastNotNullIndex + 1, i + 1, (series.getData().get(lastNotNullIndex)).getExtraValue());
            }
        }
    }

    /**
     * @param series
     * @param startIndex 포함.
     * @param endIndex   포함하지 않음. 이 앞까지
     *                   빈 값을 직선으로 채움.
     */
    private static <X, Y extends Number> void fillNullData(XYChart.Series<X, Y> series, int startIndex, int endIndex) {
        XYChart.Data<X, Y> startData = series.getData().get(startIndex - 1);
        XYChart.Data<X, Y> endData = series.getData().get(endIndex);
        Double startValue = startData.getYValue().doubleValue();
        Double endValue = endData.getYValue().doubleValue();

        for (int i = startIndex; i < endIndex; i++) {
            XYChart.Data currentData = series.getData().get(i);
            currentData.setYValue(startValue + (endValue - startValue) * (i - startIndex + 1) / (endIndex - startIndex + 1));
            currentData.setNode(new Text());
        }
    }

    /**
     * @param series
     * @param startIndex 포함.
     * @param endIndex   포함하지 않음. 이 앞까지
     * @param value      빈 값을 이 값으로 채움.
     */
    private static <X, Y> void fillNullData(XYChart.Series<X, Y> series, int startIndex, int endIndex, Object value) {
        for (int i = startIndex; i < endIndex; i++) {
            XYChart.Data currentData = series.getData().get(i);
            currentData.setYValue(value);
            currentData.setNode(new Text());
        }
    }
}

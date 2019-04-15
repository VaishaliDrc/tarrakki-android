package com.tarrakki.chartformaters;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class BarChartCustomRenderer extends BarChartRenderer {


    public BarChartCustomRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }


    @Override
    public void drawValue(Canvas c, IValueFormatter formatter, float value, Entry entry, int dataSetIndex, float x, float y, int color) {
        String text = formatter.getFormattedValue(value, entry, dataSetIndex, mViewPortHandler);
        String[] splitText;
        if (text.contains("\n")) {
            splitText = text.split("\n");
            Paint paintStyleOne = new Paint(mValuePaint);
            Paint paintStyleTwo = new Paint(mValuePaint);
            paintStyleOne.setColor(Color.BLACK);
            paintStyleTwo.setColor(Color.BLACK);
            c.drawText(splitText[0], x, y - 40f, paintStyleOne);
            c.drawText(splitText[1], x, y, paintStyleTwo);
        }
    }
}

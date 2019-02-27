
package com.tarrakki.chartformaters;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.tarrakki.R;
import com.tarrakki.api.model.ReturnsHistory;

import org.supportcompact.ktx.KDateKt;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class MyMarkerView extends MarkerView {

    private DecimalFormat mFormat = new DecimalFormat("##,##,###.00");

    private TextView tvContent;
    private ArrayList<ReturnsHistory> returnsHistory;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {
            CandleEntry ce = (CandleEntry) e;
            tvContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            //Utils.formatNumber(e.getY(), 0, true)
            try {
                int index = (int) e.getX();
                String data = KDateKt.convertTo(getReturnsHistory().get(index).getDate(), "dd MMM, yyyy");
                data += "\nNAV: " + mFormat.format(getReturnsHistory().get(index).getValue());
                tvContent.setText(data);
            } catch (Exception e1) {
                e1.printStackTrace();
                tvContent.setText("" + Utils.formatNumber(e.getY(), 0, true));
            }
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    public ArrayList<ReturnsHistory> getReturnsHistory() {
        return returnsHistory;
    }

    public MyMarkerView setReturnsHistory(ArrayList<ReturnsHistory> returnsHistory) {
        this.returnsHistory = returnsHistory;
        return this;
    }
}

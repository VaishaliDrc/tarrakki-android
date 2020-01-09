package org.supportcompact.widgets;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.AttributeSet;

public class CustomLinearLayoutManager extends LinearLayoutManager {


    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}

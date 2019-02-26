package org.supportcompact.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import org.supportcompact.R;

import static android.os.Build.VERSION_CODES;

/**
 * This is a custom view to draw dotted, vertical lines.
 * <p>
 * A regular shape drawable (like we use for horizontal lines) is usually not sufficient because
 * rotating such a (by default horizontal) line to be vertical does not recalculate the correct
 * with and height if they are set to match_parent or wrap_content.
 * <p>
 * Furthermore, this view draws actual round dots, not those fake tiny square ones like shape
 * drawables do.
 * <p>
 * A more elaborate version of this view would use custom attributes to set the color of the line
 * more dynamically, as well as the line shape, gap size, etc.
 */
public class DottedLine extends View {

    private float density;
    private Paint paint;
    private Path path;
    private PathEffect effects;

    public DottedLine(Context context) {
        super(context);
        init(context);
    }

    public DottedLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DottedLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public DottedLine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        density = getResources().getDisplayMetrics().density;
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(density * 4);
        paint.setColor(context.getResources().getColor(R.color.darker_gray));
        path = new Path();
        //array is ON and OFF distances in px (4px line then 2px space)
        effects = new DashPathEffect(new float[]{12, 4, 12, 4}, 0);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        paint.setPathEffect(effects);
        int measuredHeight = getMeasuredHeight();
        int measuredWidth = getMeasuredWidth();
        if (measuredHeight <= measuredWidth) {
            // horizontal
            path.moveTo(0, 0);
            path.lineTo(measuredWidth, 0);
            canvas.drawPath(path, paint);
        } else {
            // vertical
            path.moveTo(0, 0);
            path.lineTo(0, measuredHeight);
            canvas.drawPath(path, paint);
        }

    }
}
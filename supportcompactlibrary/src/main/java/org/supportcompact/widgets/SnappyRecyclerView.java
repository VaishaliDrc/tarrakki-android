package org.supportcompact.widgets;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by rohit on 25/2/17.
 */

public class SnappyRecyclerView extends RecyclerView {

    private Point mStartMovePoint = new Point(0, 0);
    private int mStartMovePositionFirst = 0;
    private int mStartMovePositionSecond = 0;

    private int calculatedPosition = 0;

    private Handler handler;
    private Runnable runnable;
    private boolean shouldAutoScroll = false;
    private boolean isBeingTouched = false;

    private int time = 3000;
    private SnapListener snapListener;

    public SnappyRecyclerView(Context context) {
        super(context);
        init();
    }

    public SnappyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnappyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void startAutoScroll() {
        shouldAutoScroll = true;
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, time);
    }

    public void setSnapListener(SnapListener listener) {
        snapListener = listener;
    }

    public void setShouldAutoScroll(boolean shouldAutoScroll) {
        if (shouldAutoScroll)
            startAutoScroll();
    }

    @Override
    public void smoothScrollToPosition(int position) {
        super.smoothScrollToPosition(position);
        //GtmAnalytics.instance.promotion("Banner Type", "", false);
        if (snapListener != null) {
            snapListener.onSnap(position);
        }
    }

    private void init() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, time);
                if (!shouldAutoScroll && !isBeingTouched) {
                    return;
                }
                int count = getAdapter().getItemCount() - 1;
                if (calculatedPosition == count) {
                    calculatedPosition = 0;
                    smoothScrollToPosition(calculatedPosition);
                    return;
                }
                calculatedPosition++;
                smoothScrollToPosition(calculatedPosition);
            }
        };

    }

    /*@Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!handler.getLooper().getThread().isAlive())
            startAutoScroll();
    }*/

    @Override
    protected void onDetachedFromWindow() {
        destroy();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (shouldAutoScroll)
            startAutoScroll();
    }

    public void destroy() {
        if (handler != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        final boolean ret = super.onTouchEvent(e);
        final LinearLayoutManager lm = (LinearLayoutManager) getLayoutManager();

        View childView = lm.getChildAt(0);

        if (childView == null) {
            return ret;
        }

        mStartMovePositionFirst = lm.findFirstVisibleItemPosition();
        mStartMovePositionSecond = mStartMovePositionFirst + 1;

        if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE
                && mStartMovePoint.x == 0) {

            if (lm.getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL) {
                mStartMovePoint.x = -(int) e.getX();
                mStartMovePoint.y = -(int) e.getY();
            } else {
                mStartMovePoint.x = (int) e.getX();
                mStartMovePoint.y = (int) e.getY();
            }

            isBeingTouched = true;

        }// if MotionEvent.ACTION_MOVE

        if ((e.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {

            int width = childView.getWidth();
            int currentX;

            if (lm.getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL) {
                currentX = -(int) e.getX();
            } else {
                currentX = (int) e.getX();
            }

            int xMovement = currentX - mStartMovePoint.x;
            // move back will be positive value

            boolean moveBack = xMovement > 0;

            calculatedPosition = mStartMovePositionFirst;
            if (moveBack && mStartMovePositionSecond > 0)
                calculatedPosition = mStartMovePositionSecond;

            if (Math.abs(xMovement) > (width / 4))
                calculatedPosition += moveBack ? -1 : 1;

            if (calculatedPosition >= getAdapter().getItemCount())
                calculatedPosition = getAdapter().getItemCount() - 1;

            if (calculatedPosition < 0 || getAdapter().getItemCount() == 0)
                calculatedPosition = 0;

            mStartMovePoint.x = 0;
            mStartMovePoint.y = 0;
            mStartMovePositionFirst = 0;
            mStartMovePositionSecond = 0;

            smoothScrollToPosition(calculatedPosition);

            isBeingTouched = false;

        }

        return ret;
    }

    public interface SnapListener {
        void onSnap(int position);

    }
}

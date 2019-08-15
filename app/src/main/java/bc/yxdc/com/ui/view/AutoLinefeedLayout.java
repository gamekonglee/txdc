package bc.yxdc.com.ui.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import app.txdc.shop.R;


/**
 * Created by gklee on 2017/2/27.
 */

public class AutoLinefeedLayout extends ViewGroup {

    public AutoLinefeedLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AutoLinefeedLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLinefeedLayout(Context context) {
        this(context, null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutHorizontal();
    }

    private void layoutHorizontal() {
        final int count = getChildCount();
        final int lineWidth = getMeasuredWidth() - getPaddingLeft()
                - getPaddingRight();
        int paddingTop = getPaddingTop();
        int childTop = 0;
        int childLeft = getPaddingLeft();

        int availableLineWidth = lineWidth;
        int maxLineHight = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null) {
                continue;
            } else if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                if (availableLineWidth < childWidth) {
                    availableLineWidth = lineWidth;
                    paddingTop = paddingTop + maxLineHight;
                    childLeft = getPaddingLeft();
                    maxLineHight = 0;
                }
                childTop = paddingTop;
                setChildFrame(child, childLeft, childTop, childWidth,
                        childHeight);
                childLeft += childWidth;
                availableLineWidth = availableLineWidth - childWidth;
                maxLineHight = Math.max(maxLineHight, childHeight);
            }
        }
    }

    private void setChildFrame(View child, int left, int top, int width,
                               int height) {
        child.layout(left, top, left + width, top + height);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        if (heightMode == MeasureSpec.AT_MOST||heightMode == MeasureSpec.UNSPECIFIED) {
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                    getDesiredHeight(width), MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private int getDesiredHeight(int width) {
        final int lineWidth = width - getPaddingLeft() - getPaddingRight();
        int availableLineWidth = lineWidth;
        int totalHeight = getPaddingTop() + getPaddingBottom();
        int lineHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();
            if (availableLineWidth < childWidth) {
                availableLineWidth = lineWidth;
                totalHeight = totalHeight + lineHeight;
                lineHeight = 0;
            }
            availableLineWidth = availableLineWidth - childWidth;
            lineHeight = Math.max(childHeight, lineHeight);
        }
        totalHeight = totalHeight + lineHeight;
        return totalHeight;
    }
    @Override
    protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public android.view.ViewGroup.LayoutParams generateLayoutParams(
            AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(
            android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public int gravity = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray ta = c.obtainStyledAttributes(attrs,
                    R.styleable.SlideGroup);

            gravity = ta.getInt(R.styleable.SlideGroup_layout_gravity, -1);

            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            this(width, height, -1);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }
}

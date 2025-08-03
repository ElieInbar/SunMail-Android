package com.example.sunmail.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int x = 0;
        int y = 0;
        int rowHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                if (x + childWidth > parentWidth) {
                    x = 0;
                    y += rowHeight;
                    rowHeight = 0;
                }

                x += childWidth;
                rowHeight = Math.max(rowHeight, childHeight);
            }
        }

        y += rowHeight;
        setMeasuredDimension(parentWidth, y);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int parentWidth = r - l;
        int x = 0;
        int y = 0;
        int rowHeight = 0;

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();

                if (x + childWidth > parentWidth) {
                    x = 0;
                    y += rowHeight;
                    rowHeight = 0;
                }

                child.layout(x, y, x + childWidth, y + childHeight);
                x += childWidth;
                rowHeight = Math.max(rowHeight, childHeight);
            }
        }
    }
}

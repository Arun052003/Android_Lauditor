package com.digicoffer.lauditor.Dashboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import kotlin.Pair;

public class ArcLayoutManager extends RecyclerView.LayoutManager {
    private final Context context;
    private int horizontalOffset = 0;

    public ArcLayoutManager(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        horizontalOffset += dx;
        fill(recycler, state);
        return dx;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (recycler == null) return;

        detachAndScrapAttachedViews(recycler);

        for (int itemIndex = 0; itemIndex < getItemCount(); itemIndex++) {
            View view = recycler.getViewForPosition(itemIndex);
            addView(view);

            int viewWidth = pxFromDp(50);
            int viewHeight = pxFromDp(50);

            int left = (itemIndex * viewWidth) - horizontalOffset;
            int right = left + viewWidth;
            Pair<Integer, Double> topAlphaPair = computeYComponent((left + right) / 2, viewHeight);
            int top = topAlphaPair.getFirst();
            int bottom = top + viewHeight;

            float alpha = (float) ((topAlphaPair.getSecond() * (180 / Math.PI)) - 90);
            view.setRotation(alpha);

            measureChildWithMargins(view, viewWidth, viewHeight);
            layoutDecoratedWithMargins(view, left, top, right, bottom);
        }
    }

    private Pair<Integer, Double> computeYComponent(float viewCenterX, float h) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        double s = screenWidth / 2.0;
        double radius = (h * h + s * s) / (h * 2);

        double xScreenFraction = viewCenterX / screenWidth;
        double beta = Math.acos(s / radius);

        double alpha = beta + (xScreenFraction * (Math.PI - (2 * beta)));
        double yComponent = radius - (radius * Math.sin(alpha));

        return new Pair<>((int) yComponent, alpha);
    }

    private int pxFromDp(float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}

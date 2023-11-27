package com.digicoffer.lauditor.Dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class SemiCircleLayoutManager extends RecyclerView.LayoutManager {

    private static final int VIEW_RADIUS_DP = 200; // Radius of the semi-circle
//    private static final int VIEW_RADIUS_DP = 200; // Radius of the semi-circle
    private RecyclerView recyclerView;
    public SemiCircleLayoutManager(Context context) {
        this.recyclerView = new RecyclerView(context);
    }
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);

        int itemCount = getItemCount();
        if (itemCount == 0) {
            return;
        }

        int viewRadius = dpToPx(VIEW_RADIUS_DP); // Convert dp to pixels
        int centerX = getWidth() / 2;
        int centerY = getHeight();

        double angleRadian = Math.PI / (itemCount - 1);
        for (int i = 0; i < itemCount; i++) {
            View view = recycler.getViewForPosition(i);
            addView(view);

            int offsetX = (int) (viewRadius * Math.sin(i * angleRadian));
            int offsetY = (int) (viewRadius * Math.cos(i * angleRadian));

            int left = centerX + offsetX - view.getMeasuredWidth() / 2;
            int top = centerY - offsetY - view.getMeasuredHeight() / 2;
            int right = left + view.getMeasuredWidth();
            int bottom = top + view.getMeasuredHeight();

            layoutDecoratedWithMargins(view, left, top, right, bottom);
        }
    }

    private int dpToPx(int dp) {
        float density = recyclerView.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }
}
package com.digicoffer.lauditor.Dashboard;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class CircularLayoutManager extends RecyclerView.LayoutManager {

        private int radius; // Radius for the circular layout
        private float anglePerItem; // Angle between each item

        public CircularLayoutManager(Context context, int radius) {
                this.radius = radius;
        }

        @Override
        public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                if (state.isPreLayout()) return;

                detachAndScrapAttachedViews(recycler);

                int itemCount = getItemCount();
                if (itemCount == 0) return;

                anglePerItem = (float) (2 * Math.PI / itemCount); // Calculate angle per item

                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                for (int i = 0; i < itemCount; i++) {
                        View view = recycler.getViewForPosition(i);
                        addView(view);

                        int itemWidth = getDecoratedMeasuredWidth(view);
                        int itemHeight = getDecoratedMeasuredHeight(view);

                        int offsetX = (int) (radius * Math.cos(anglePerItem * i)); // X-coordinate calculation
                        int offsetY = (int) (radius * Math.sin(anglePerItem * i)); // Y-coordinate calculation

                        int left = centerX + offsetX - itemWidth / 2;
                        int top = centerY + offsetY - itemHeight / 2;
                        int right = left + itemWidth;
                        int bottom = top + itemHeight;

                        layoutDecorated(view, left, top, right, bottom);
                }
        }
}
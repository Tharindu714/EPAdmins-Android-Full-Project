package com.deltacodex.epadmins.Adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing; // Space in pixels

    public GridSpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = spacing / 2;
        outRect.right = spacing / 2;
        outRect.top = spacing / 2;
        outRect.bottom = spacing / 2;
    }
}

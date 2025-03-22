package com.deltacodex.epadmins.Adapter;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDeco extends RecyclerView.ItemDecoration {

    private final int space;

    public SpaceItemDeco(int space) {
        this.space = space; // space in pixels
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // Add space to the left, top, right, and bottom of each item
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Optionally, add extra space at the top of the first item (or any item)
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space; // This will add space above the first item, remove if not needed
        }
    }
}
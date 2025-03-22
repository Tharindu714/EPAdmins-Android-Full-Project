package com.deltacodex.epadmins.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.PromotionalItem;

import java.util.List;

public class PromotionalAdapter extends RecyclerView.Adapter<PromotionalAdapter.ViewHolder> {
    private List<PromotionalItem> items;

    public PromotionalAdapter(List<PromotionalItem> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url = items.get(position).getUrl();
        Glide.with(holder.itemView.getContext()).load(url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

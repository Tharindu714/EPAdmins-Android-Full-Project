package com.deltacodex.epadmins.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deltacodex.epadmins.R;

public class SpinnerAdapter extends BaseAdapter {
    private final String[] categories = {"TV Shows", "Movies", "Games"};
    private final int[] icons = {R.drawable.ic_tv, R.drawable.ic_movie, R.drawable.ic_game};
    private final LayoutInflater inflater;

    public SpinnerAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return categories.length;
    }

    @Override
    public Object getItem(int position) {
        return categories[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Get the normal selected view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    // Get the dropdown view
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    // Common method for view creation
    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item_collection, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.spinnerIcon);
        TextView text = convertView.findViewById(R.id.spinnerText);

        if (icon != null) {
            icon.setImageResource(icons[position]);
        } else {
            Log.e("SpinnerAdapter", "ImageView is null. Check spinner_item.xml ID.");
        }

        text.setText(categories[position]);
        return convertView;
    }
}


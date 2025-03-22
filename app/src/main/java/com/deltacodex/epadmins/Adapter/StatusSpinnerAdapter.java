package com.deltacodex.epadmins.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.StatusItem;

import java.util.List;

public class StatusSpinnerAdapter extends ArrayAdapter<StatusItem> {
    private final Context context;
    private final List<StatusItem> statusList;

    public StatusSpinnerAdapter(Context context, List<StatusItem> statusList) {
        super(context, R.layout.spinner_item, statusList);
        this.context = context;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.statusIcon);
        TextView text = convertView.findViewById(R.id.statusText);

        StatusItem item = statusList.get(position);
        icon.setImageResource(item.getStatusIcon());
        text.setText(item.getStatusName());

        return convertView;
    }
}

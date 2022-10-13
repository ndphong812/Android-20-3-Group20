package com.example.messenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterSettings extends BaseAdapter {
    private Context context;
    private int layout;
    private String[] labels;

    public AdapterSettings(Context context, int layout, String[] labels) {
        this.context = context;
        this.layout = layout;
        this.labels = labels;
    }

    @Override
    public int getCount() {
        return labels.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout, null);

        String label = labels[pos];
        TextView txtLabel = convertView.findViewById(R.id.settingsLabel);

        txtLabel.setText(label);
        return convertView;
    }
}

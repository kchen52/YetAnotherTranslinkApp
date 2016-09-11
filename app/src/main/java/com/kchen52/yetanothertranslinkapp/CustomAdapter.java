package com.kchen52.yetanothertranslinkapp;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<Model> {
    Model[] modelItems = null;
    Context context;

    public CustomAdapter(Context context, Model[] resource) {
        super(context, R.layout.bus_row, resource);
        this.context = context;
        this.modelItems = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.bus_row, parent, false);
        TextView name = (TextView) convertView.findViewById(R.id.busRowTextView);
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.busRowCheckBox);
        name.setText(modelItems[position].getName());
        if (modelItems[position].getValue() == 1) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
        return convertView;
    }

}

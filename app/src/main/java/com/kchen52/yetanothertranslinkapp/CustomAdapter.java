package com.kchen52.yetanothertranslinkapp;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAdapter extends ArrayAdapter<Model> {
    Model[] modelItems = null;
    Context context;
    SharedPreferences sharedPref;

    public CustomAdapter(Context context, Model[] resource) {
        super(context, R.layout.bus_row, resource);
        this.context = context;
        this.modelItems = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.bus_row, parent, false);

        final TextView name = (TextView) convertView.findViewById(R.id.busRowTextView);
        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.busRowCheckBox);
        cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

                String savedBuses = sharedPref.getString(context.getString(R.string.saved_buses_requested), context.getString(R.string.saved_buses_requested_default));
                String checkedBusText = (String) name.getText();
                String checkedBusNumber = checkedBusText.split(" - ")[0];
                if (cb.isChecked()) {
                    // Check if the bus exists in the list of buses, and add it if not
                    if (!busAlreadySaved(checkedBusNumber, savedBuses)) {
                        // Append the current bus to the end of savedBuses, and save it to the sharedpref
                        String busNumber = ((String)name.getText()).split(" - ")[0];
                        if (savedBuses.equals("")) {
                            savedBuses = busNumber;
                        } else {
                            savedBuses = savedBuses + ", " + busNumber;
                        }

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(context.getString(R.string.saved_buses_requested), savedBuses);
                        editor.commit();
                    }
                } else {
                    // Check if the bus exists in the list of buses, and remove it if so
                    if (busAlreadySaved(checkedBusNumber, savedBuses)) {
                        StringBuilder builder = new StringBuilder();

                        // Not the best way to do this, but we need a comma after each element, except the first
                        int index = 0;
                        for (String bus : savedBuses.split(", ")) {
                            if (!bus.equals(checkedBusNumber)) {
                                if (index > 0) {
                                    builder.append(", ");
                                }
                                builder.append(bus);
                                index++;
                            }
                        }
                        String newFormattedBusList = builder.toString();

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(context.getString(R.string.saved_buses_requested), newFormattedBusList);
                        editor.commit();
                    }
                }
            }
        });
        name.setText(modelItems[position].getName());
        if (modelItems[position].getValue() == 1) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
        return convertView;
    }

    private boolean busAlreadySaved(String busNumber, String savedBuses) {
        String[] buses = savedBuses.split(", ");
        for (String bus : buses) {
            if (busNumber.equals(bus)) {
                return true;
            }
        }
        return false;
    }
}

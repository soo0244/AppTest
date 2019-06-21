package com.example.apptest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SpinnerAdapter extends BaseAdapter {

    String amountSpinner[];
    Context context;

    public SpinnerAdapter(String[] amountSpinner, Context context) {
        this.amountSpinner = amountSpinner;
        this.context = context;
    }

    @Override
    public int getCount() {
        return amountSpinner.length;
    }

    @Override
    public Object getItem(int position) {
        return amountSpinner[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner, null);
        }

        TextView textView = (TextView)convertView.findViewById(R.id.text_spinner);
        textView.setText(amountSpinner[position]);

        return  convertView;
    }
}

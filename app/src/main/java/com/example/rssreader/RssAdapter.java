package com.example.rssreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class RssAdapter extends ArrayAdapter<RssItem> {

    public RssAdapter(Context context, ArrayList<RssItem> rssItems) {
        super(context, 0, rssItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RssItem rssItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }
        TextView titleTextView = convertView.findViewById(android.R.id.text1);
        TextView descriptionTextView = convertView.findViewById(android.R.id.text2);
        titleTextView.setText(rssItem.getTitle());
        descriptionTextView.setText(rssItem.getDescription());
        return convertView;
    }
}
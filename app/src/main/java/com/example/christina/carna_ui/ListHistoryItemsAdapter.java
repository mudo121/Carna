package com.example.christina.carna_ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class ListHistoryItemsAdapter extends ArrayAdapter<ListHistoryItem> {

    private final ArrayList<ListHistoryItem> mItems;
    private final Context mContext;


    public ListHistoryItemsAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.mContext = context;
        this.mItems = new ArrayList<ListHistoryItem>();
    }


    public void addItem(ListHistoryItem item) {
        mItems.add(item);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            try {
                view = getInflator().inflate(R.layout.list_history_item, parent, false);
            } catch (Exception e) {
                return null; //TODO: handle exception
            }
        }

        if (mItems.size() == 0) {
            return view;
        }
        ListHistoryItem item = mItems.get(position);
        if (item != null) {

            TextView acountNameView = (TextView) view.findViewById(R.id.item_value);
            if (acountNameView != null) {
                acountNameView.setText(item.getItemName());
            }

            TextView itemKeyView = (TextView) view.findViewById(R.id.item_date);
            if (itemKeyView != null) {
                itemKeyView.setText(item.getItemKey());
            }
        }

        return view;
    }


    private LayoutInflater getInflator() {
        return (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
}

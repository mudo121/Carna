package com.example.christina.carna_ui.listviewadapter;

/**
 * Created by raphy-laptop on 20.06.2016.
 */
public class ListHistoryItem {
    private final String mItemValue;
    private final String mItemDate;


    public ListHistoryItem(String itemValue, String itemDate) {
        mItemValue = itemValue;
        mItemDate = itemDate;
    }


    public String getItemKey() {
        return mItemValue;
    }


    public String getItemName() {
        return mItemDate;
    }
}

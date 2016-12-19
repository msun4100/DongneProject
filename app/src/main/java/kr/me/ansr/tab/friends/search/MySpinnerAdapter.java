package kr.me.ansr.tab.friends.search;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KMS on 2016-07-13.
 */
public class MySpinnerAdapter extends BaseAdapter{
    List<String> items = new ArrayList<String>();
    public void add(String s) {
        items.add(s);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MySpinnerItemView tv;
        if (convertView == null) {
            tv = new MySpinnerItemView(parent.getContext());
        } else {
            tv = (MySpinnerItemView)convertView;
        }
        tv.setItemData(items.get(position), position);
//        tv.setBackgroundColor();
        return tv;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        MySpinnerItemView tv;
        if (convertView == null) {
            tv = new MySpinnerItemView(parent.getContext());
        } else {
            tv = (MySpinnerItemView)convertView;
        }
        tv.setItemData(items.get(position), position);
        tv.setBackgroundColor(Color.WHITE);

        return tv;
    }
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        TextView tv;
//        if (convertView == null) {
//            tv = new TextView(parent.getContext());
//        } else {
//            tv = (TextView)convertView;
//        }
//        tv.setText(items.get(position));
//        return tv;
//    }
//
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//        TextView tv;
//        if (convertView == null) {
//            tv = new TextView(parent.getContext());
//        } else {
//            tv = (TextView)convertView;
//        }
//        tv.setText(items.get(position));
//        tv.setBackgroundColor(Color.WHITE);
//
//        return tv;
//    }
}

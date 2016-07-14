package kr.me.ansr.login.autocomplete.ex;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;


//simple array imem 기본 레이아웃 써서 아답터 사용안함. 커스텀할때 써야될듯?
public class MyAdapter extends BaseAdapter implements Filterable {
    List<MyData> originalItems = new ArrayList<MyData>();
    List<MyData> items = null;

    @Override
    public int getCount() {
        if (items == null) return 0;
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
        ItemView view;
        if (convertView == null) {
            view = new ItemView(parent.getContext());
        } else {
            view = (ItemView)convertView;
        }
        view.setData(items.get(position));
        return view;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new MyFilter();
        }
        return filter;
    }

    MyFilter filter;

    public void add(MyData data) {
        originalItems.add(data);
    }

    class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                FilterResults result = new FilterResults();
                List<MyData> list = new ArrayList<MyData>();
                String text = constraint.toString().toLowerCase();
                for (MyData data : originalItems) {
                    String compareText = data.title.toLowerCase();
                    if (compareText.contains(text)) {
                        list.add(data);
                    }
                }
                result.values = list;
                result.count = list.size();
                return result;
            }
            return null;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (constraint != null && results != null && results.count > 0) {
                List<MyData> list = (List<MyData>)results.values;
                items = list;
                notifyDataSetChanged();
            } else {
                items = null;
                notifyDataSetChanged();
                notifyDataSetInvalidated();
            }
        }
    }
}

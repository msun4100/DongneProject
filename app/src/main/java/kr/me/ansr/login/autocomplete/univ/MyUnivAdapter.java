package kr.me.ansr.login.autocomplete.univ;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;


//simple array imem 기본 레이아웃 써서 아답터 사용안함. 커스텀할때 써야될듯?
public class MyUnivAdapter extends BaseAdapter implements Filterable {
    List<UnivResult> originalItems = new ArrayList<UnivResult>();
    List<UnivResult> items = null;

    public void clearAll() {
        if(originalItems != null){
            originalItems.clear();
            notifyDataSetChanged();
        }
    }

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

    public int getUnivId(int position){
        return items.get(position).univId;
    }
    public String getUnivName(int position){
        return items.get(position).univname;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UnivItemView view;
        if (convertView == null) {
            view = new UnivItemView(parent.getContext());
        } else {
            view = (UnivItemView)convertView;
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

    public void add(UnivResult data) {
        originalItems.add(data);
    }

    class MyFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                FilterResults result = new FilterResults();
                List<UnivResult> list = new ArrayList<UnivResult>();
                String text = constraint.toString().toLowerCase();
                for (UnivResult data : originalItems) {
                    String compareText = data.univname.toLowerCase();
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
                List<UnivResult> list = (List<UnivResult>)results.values;
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

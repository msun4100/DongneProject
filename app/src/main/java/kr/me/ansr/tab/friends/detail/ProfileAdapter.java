package kr.me.ansr.tab.friends.detail;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.tab.friends.model.Desc;
import kr.me.ansr.tab.friends.model.DescView;
import kr.me.ansr.tab.friends.model.ProfileItem;
import kr.me.ansr.tab.friends.model.Sns;
import kr.me.ansr.tab.friends.model.SnsView;

/**
 * Created by KMS on 2016-08-05.
 */
public class ProfileAdapter extends BaseAdapter {
    List<ProfileItem> items = new ArrayList<ProfileItem>();

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int TYPE_DESC = 0;
    private static final int TYPE_SNS = 1;



    public void add(ProfileItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        ProfileItem message = items.get(position);
        if (message instanceof Sns) {
            return TYPE_SNS;
        } else {
            return TYPE_DESC;
        }

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
        switch (getItemViewType(position)) {
            case TYPE_DESC: {
                DescView view;
                if (convertView != null && convertView instanceof DescView) {
                    view = (DescView)convertView;
                } else {
                    view = new DescView(parent.getContext());
                }
                view.setData((Desc)items.get(position));
                return view;
            }
            default: {
                SnsView view;
                if (convertView != null && convertView instanceof SnsView) {
                    view = (SnsView)convertView;
                } else {
                    view = new SnsView(parent.getContext());
                }
                view.setData((Sns) items.get(position));
                return view;
            }

        }
    }
}


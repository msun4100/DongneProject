package kr.me.ansr.tab.board.preview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by KMS on 2016-07-27.
 */
public class PreReplyAdapter extends BaseAdapter
    implements Serializable, PreReplyItemView.OnLikeClickListener{

        public interface OnAdapterItemClickListener {
            public void onAdapterItemClick(PreReplyAdapter adapter, View view, PreReply item, int type);
        }

        OnAdapterItemClickListener mListener;

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mListener = listener;
    }

    //	===============
    ArrayList<PreReply> items = new ArrayList<PreReply>();
    Context mContext;
    int totalCount;
    String keyword;

    public PreReplyAdapter(Context context) {
        mContext = context;
    }

    public void add(PreReply item){
        items.add(item);
        notifyDataSetChanged();
    }
    public void addAll(ArrayList<PreReply> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }
    public void clear() {
        items.clear();
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
        PreReplyItemView view;
        if (convertView == null) {
            view = new PreReplyItemView(mContext);
            view.setOnLikeClickListener(this);
        } else {
            view = (PreReplyItemView)convertView;
        }
        view.setItemData(items.get(position));
        return view;
    }

    @Override
    public void onLikeClick(View v, PreReply item, int type) {
        if (mListener != null) {
            mListener.onAdapterItemClick(this, v, item, type);
        }
    }
}

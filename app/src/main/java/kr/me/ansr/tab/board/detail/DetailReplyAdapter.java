package kr.me.ansr.tab.board.detail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.Serializable;
import java.util.ArrayList;

import kr.me.ansr.R;
import kr.me.ansr.tab.board.reply.ReplyResult;

/**
 * Created by KMS on 2016-07-27.
 */
public class DetailReplyAdapter extends BaseAdapter
    implements Serializable, DetailReplyItemView.OnLikeClickListener{

    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(DetailReplyAdapter adapter, View view, ReplyResult item, int type);
    }
    OnAdapterItemClickListener mListener;

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mListener = listener;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    //	===============
    ArrayList<ReplyResult> items = new ArrayList<ReplyResult>();
    Context mContext;
    int totalCount;

    public DetailReplyAdapter(Context context) {
        mContext = context;
    }

    public void add(ReplyResult item){
        items.add(item);
        notifyDataSetChanged();
    }
    public void addAll(ArrayList<ReplyResult> items) {
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
        DetailReplyItemView view;
        if (convertView == null) {
            view = new DetailReplyItemView(mContext);
            view.setOnLikeClickListener(this);
        } else {
            view = (DetailReplyItemView)convertView;
        }
        view.setItemData(items.get(position));
        return view;
    }

    @Override
    public void onLikeClick(View v, ReplyResult item, int type) {
        if (mListener != null) {
            mListener.onAdapterItemClick(this, v, item, type);
        }
    }


    public void setLike(int position, int userId){
        ReplyResult mItem = ((ReplyResult)getItem(position));
        if(mItem.likes.contains(userId)){
            if(mItem.likeCount == 0) return;
            else {
                mItem.likeCount--;
                int idx = mItem.likes.lastIndexOf(userId);
                mItem.likes.remove(idx);
            }
        } else {
            mItem.likeCount++;
            mItem.likes.add(userId);    //add 때는 indexOf 하면 현재 배열에 없으니까 -1 리턴 됨.
        }
        notifyDataSetChanged();
    }
    public void setLike(ReplyResult mItem, int userId){
//        ReplyResult mItem = ((ReplyResult)getItem(position));
        if(mItem.likes.contains(userId)){
            if(mItem.likeCount == 0) return;
            else {
                mItem.likeCount--;
                int idx = mItem.likes.lastIndexOf(userId);
                mItem.likes.remove(idx);
            }
        } else {
            mItem.likeCount++;
            mItem.likes.add(userId);    //add 때는 indexOf 하면 현재 배열에 없으니까 -1 리턴 됨.
        }
        notifyDataSetChanged();
    }

}

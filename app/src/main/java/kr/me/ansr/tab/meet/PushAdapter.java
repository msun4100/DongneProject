package kr.me.ansr.tab.meet;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.me.ansr.R;
import kr.me.ansr.database.Push;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;

/**
 * Created by KMS on 2016-09-22.
 */
public class PushAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnItemClickListener, OnItemLongClickListener, ItemViewHolder.OnLikeClickListener{
    public List<Push> items = new ArrayList<Push>();



    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(PushAdapter adapter, View view, Push item, int type);
    }
    OnAdapterItemClickListener mListener;
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onLikeClick(View v, Push item, int type) {
        if (mListener != null) {
            mListener.onAdapterItemClick(this, v, item, type);
        }
    }

    //start recyclerView.OnItemClick implements
    OnItemClickListener itemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }
    @Override
    public void onItemClick(View view, int position) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(view, position);
        }
    }//end of recyclerView.OnItemClick implements

    OnItemLongClickListener itemLongClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        itemLongClickListener = listener;
    }
    @Override
    public void onItemLongClick(View view, int position) {
        if (itemLongClickListener != null) {
            itemLongClickListener.onItemLongClick(view, position);
        }
    }
    public void add(Push child){
        items.add(child);
        notifyDataSetChanged();
    }

    public void removeItem(Push child){
        items.remove(child);
        notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Push> items) {
//        this.items.get(GROUP_FRIENDS).children.addAll(items);
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void setItemBackGround(int position){
        if(position < 0 || position > getItemCount()){
            return;
        }
        getItem(position).bgColor = 1;
        notifyDataSetChanged();
    }


    private int totalCount;
    public void setTotalCount(int count){
        this.totalCount = count;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public static final int VIEW_TYPE_SECTION_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_feed_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view, parent.getContext());
        holder.setOnItemClickListener(this);
        holder.setOnItemLongClickListener(this);
        holder.setOnLikeClickListener(this);
        return holder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Push child = items.get(position);
        ((ItemViewHolder)holder).setChildItem(child);
        return;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public Push getItem(int position) {
        if(position < 0 || position > getItemCount()){
            return null;
        }
        return items.get(position);
    }

}

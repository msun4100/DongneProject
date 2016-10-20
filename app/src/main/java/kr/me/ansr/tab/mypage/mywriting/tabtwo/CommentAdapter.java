package kr.me.ansr.tab.mypage.mywriting.tabtwo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.R;

import kr.me.ansr.tab.board.reply.ReplyResult;



public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements kr.me.ansr.tab.friends.recycler.OnItemClickListener, kr.me.ansr.tab.friends.recycler.OnItemLongClickListener, ItemViewHolder.OnLikeClickListener{
    public List<ReplyResult> items = new ArrayList<ReplyResult>();

    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(CommentAdapter adapter, View view, ReplyResult item, int type);
    }
    OnAdapterItemClickListener mListener;
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onLikeClick(View v, ReplyResult item, int type) {
        if (mListener != null) {
            mListener.onAdapterItemClick(this, v, item, type);
        }
    }

    //start recyclerView.OnItemClick implements
    kr.me.ansr.tab.friends.recycler.OnItemClickListener itemClickListener;
    public void setOnItemClickListener(kr.me.ansr.tab.friends.recycler.OnItemClickListener listener) {
        itemClickListener = listener;
    }
    @Override
    public void onItemClick(View view, int position) {
        if (itemClickListener != null) {
            itemClickListener.onItemClick(view, position);
        }
    }//end of recyclerView.OnItemClick implements

    kr.me.ansr.tab.friends.recycler.OnItemLongClickListener itemLongClickListener;
    public void setOnItemLongClickListener(kr.me.ansr.tab.friends.recycler.OnItemLongClickListener listener){
        itemLongClickListener = listener;
    }
    @Override
    public void onItemLongClick(View view, int position) {
        if (itemLongClickListener != null) {
            itemLongClickListener.onItemLongClick(view, position);
        }
    }

    public void clearAll(){
        this.items.clear();
        notifyDataSetChanged();
    }

    public void findOneAndModify(ReplyResult child){
        for(ReplyResult rr : items){
            if(rr.userId == child.userId){
                int index = items.indexOf(rr);
                if(index != -1){
                    items.get(index).userId = child.userId;
                    items.get(index).replies = child.replies;
                    items.get(index)._id = child._id;
                    items.get(index).boardId = child.boardId;
                    items.get(index).body = child.body;
                    items.get(index).likeCount = child.likeCount;
                    items.get(index).likes = child.likes;
                    items.get(index).type = child.type;
                    items.get(index).updatedAt = child.updatedAt;
                    items.get(index).username = child.username;
                }
                break;
            }
        }
        notifyDataSetChanged();
    }
    public void add(ReplyResult child){
        items.add(child);
        notifyDataSetChanged();
    }

    public void removeItem(ReplyResult child){
        items.remove(child);
        notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ReplyResult> items) {
//        this.items.get(GROUP_FRIENDS).children.addAll(items);
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void setItemBackGround(int position){
        if(position < 0 || position > getItemCount()){
            return;
        }
//        getItem(position).bgColor = 1;
        notifyDataSetChanged();
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
        ReplyResult child = items.get(position);
        ((ItemViewHolder)holder).setChildItem(child);
        return;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public ReplyResult getItem(int position) {
        if(position < 0 || position > getItemCount()){
            return null;
        }
        return items.get(position);
    }

    private int totalCount;
    public int getTotalCount(){
        return this.totalCount;
    }
    public void setTotalCount(int count){
        this.totalCount = count;
    }

}
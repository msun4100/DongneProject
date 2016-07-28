package kr.me.ansr.tab.board.one;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-07-27.
 */
public class BoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnItemClickListener, OnItemLongClickListener, BoardViewHolder.OnLikeClickListener{
    public List<BoardResult> items = new ArrayList<BoardResult>();

    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(BoardAdapter adapter, View view, int position, BoardResult item, int type);
    }
    OnAdapterItemClickListener mListener;
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onLikeClick(View v, int position, BoardResult item, int type) {
        if (mListener != null) {
            mListener.onAdapterItemClick(this, v, position,item, type);
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

    public void add(BoardResult item){
        items.add(item);
        notifyDataSetChanged();
    }
    public void clearAll() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<BoardResult> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        view = inflater.inflate(R.layout.view_board_layout, parent, false);
        BoardViewHolder holder = new BoardViewHolder(view, parent.getContext());
        holder.setOnItemClickListener(this);
        holder.setOnItemLongClickListener(this);
        holder.setOnLikeClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((BoardViewHolder)holder).setBoardItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public BoardResult getItem(int position) {
        if (position < 0 || position >= items.size()) return null;
        return items.get(position);
    }

    private int totalCount;
    public int getTotalCount(){
        return this.totalCount;
    }
    public void setTotalCount(int count){
        this.totalCount = count;
    }
    //like
    public void setLike(int position, int userId){
        if(getItem(position).likes.contains(userId)){
            if(getItem(position).likeCount == 0) return;
            else {
                getItem(position).likeCount--;
                //remove(userId) 하면 인덱스기반 remove(index(===userId))가 실행되서 boundException 발생하기 때문에 idx 따로 처리
                //ArrayList<Integer> likes 니까 new Integer(userId) 로 넣어야 되나?
                int idx = getItem(position).likes.lastIndexOf(userId);
                getItem(position).likes.remove(idx);
            }
        } else {
            getItem(position).likeCount++;
            getItem(position).likes.add(userId);    //add는 indexOf 하면 현재 배열에 없으니까 -1 리턴 됨.
        }
        notifyDataSetChanged();
    }
}


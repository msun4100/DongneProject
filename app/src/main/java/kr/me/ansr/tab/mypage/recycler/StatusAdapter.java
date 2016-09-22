package kr.me.ansr.tab.mypage.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.me.ansr.R;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.GroupItem;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;
import kr.me.ansr.tab.friends.recycler.SectionHeaderViewHolder;

/**
 * Created by KMS on 2016-09-06.
 */
public class StatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnItemClickListener, OnItemLongClickListener, ItemViewHolder.OnLikeClickListener{
//    public List<GroupItem> items = new ArrayList<GroupItem>();
    public List<FriendsResult> items = new ArrayList<FriendsResult>();

    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(StatusAdapter adapter, View view, FriendsResult item, int type);
    }
    OnAdapterItemClickListener mListener;
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onLikeClick(View v, FriendsResult item, int type) {
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
    //for individual listener
    public void put(FriendsResult child){
        items.add(child);
        notifyDataSetChanged();
    }
    //status가 00으로 요청했을때도 받는 status는 0 이기때문에
    //그에따른 스트링 처리를 위해서 ReceiveFragment에서만 아래 함수를 오버라이딩해서 사용
    public void put(FriendsResult child, int status){
        child.status = status;
        items.add(child);
        notifyDataSetChanged();
    }
    public void removeItem(FriendsResult child){
        items.remove(child);
        notifyDataSetChanged();
    }

//    public void setLike(int position, int userId){
//        if(getItem(position).likes.contains(userId)){
//            if(getItem(position).likeCount == 0) return;
//            else {
//                getItem(position).likeCount--;
//                //remove(userId) 하면 인덱스기반 remove(index(===userId))가 실행되서 boundException 발생하기 때문에 idx 따로 처리
//                //ArrayList<Integer> likes 니까 new Integer(userId) 로 넣어야 되나?
//                int idx = getItem(position).likes.lastIndexOf(userId);
//                getItem(position).likes.remove(idx);
//            }
//        } else {
//            getItem(position).likeCount++;
//            getItem(position).likes.add(userId);    //add는 indexOf 하면 현재 배열에 없으니까 -1 리턴 됨.
//        }
//        notifyDataSetChanged();
//    }

    public void addAllFriends(List<FriendsResult> items) {
//        this.items.get(GROUP_FRIENDS).children.addAll(items);
        this.items.addAll(items);
        notifyDataSetChanged();
    }


    public static final int VIEW_TYPE_SECTION_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_status_child, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view, parent.getContext());
        holder.setOnItemClickListener(this);
        holder.setOnItemLongClickListener(this);
        holder.setOnLikeClickListener(this);
        return holder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FriendsResult child = items.get(position);
        ((ItemViewHolder)holder).setChildItem(child);
        return;
//        for (int i = 0; i < items.size(); i++) {
//            GroupItem g = items.get(i);
//            if (position < 1) {
//                ((SectionHeaderViewHolder)holder).setGroupItem(g);
//                return;
//            }
//            position--;
//            int childCount = g.children.size();
//            if (position < childCount) {
//                FriendsResult child = g.children.get(position);
//                ((ItemViewHolder)holder).setChildItem(child);
//                return;
//            }
//            position-=childCount;
//        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    private int totalCount; //학교사람들 전체 토탈
    public int getTotalCount(){
//        return getItemCount() - this.items.size();  //total - 그룹 갯수
        return this.totalCount;
    }
    public void setTotalCount(int count){
        this.totalCount = count;
    }

    public FriendsResult getItem(int position) {
        if(position < 0 || position > getItemCount()){
            return null;
        }
        return items.get(position);
    }


}

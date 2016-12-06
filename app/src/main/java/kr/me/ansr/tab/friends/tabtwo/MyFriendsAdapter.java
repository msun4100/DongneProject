package kr.me.ansr.tab.friends.tabtwo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.me.ansr.R;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.GroupItem;
import kr.me.ansr.tab.friends.recycler.ItemViewHolder;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;
import kr.me.ansr.tab.friends.recycler.SectionHeaderViewHolder;

/**
 * Created by KMS on 2016-07-25.
 */
public class MyFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements  OnItemLongClickListener, OnItemClickListener, ItemViewHolder.OnLikeClickListener{
    public List<GroupItem> items = new ArrayList<GroupItem>();
    public int blockCount = 0;

    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(MyFriendsAdapter adapter, View view, int position, FriendsResult item, int type);
    }
    OnAdapterItemClickListener mListener;
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onLikeClick(View v, int position, FriendsResult item, int type) {
        if (mListener != null) {
            mListener.onAdapterItemClick(this, v, position, item, type);

        }
    }

    Random r = new Random();

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
    public void findOneAndModify(FriendsResult child){
        for (GroupItem g : items) {
            for(FriendsResult fr : g.children){
                if(fr.userId == child.userId){
                    int index = g.children.indexOf(fr);
                    if(index != -1){
                        Log.e("MyFriendsAdapter:", "index:"+index +" "+fr.toString());
                        g.children.get(index).status = child.status;
                        g.children.get(index).univ = child.univ;
                        g.children.get(index).sns = child.sns;
                        g.children.get(index).desc = child.desc;
                        g.children.get(index).job = child.job;
                        g.children.get(index).pic = child.pic;
                        g.children.get(index).updatedAt = child.updatedAt;
                    }
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void removeItem(FriendsResult child){
//        this.items.get(GROUP_FRIENDS).children.remove(child);
        for (GroupItem g : items) {
            for (FriendsResult fr : g.children) {
                if(fr.userId == child.userId){
                    int index = g.children.indexOf(fr);
                    g.children.remove(index);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }


    public void put(String groupName, FriendsResult child) {
        GroupItem group = null;
        for (GroupItem g : items) {
            if (g.groupName.equals(groupName)) {
                group = g;
                break;
            }
        }
        if (group == null) {
            group = new GroupItem();
            group.groupName = groupName;
            items.add(group);
        }
        if(child != null){
            group.children.add(child);
        }
        notifyDataSetChanged();
    }

    public void clearAllFriends() {
//        items.clear();
        if(items.size() < 2){
            return;
        }
        items.get(GROUP_FRIENDS).children.clear();
        notifyDataSetChanged();
    }

    public void addAllFriends(List<FriendsResult> items) {
        this.items.get(GROUP_FRIENDS).children.addAll(items);
        notifyDataSetChanged();
    }

    public void addAll(List<GroupItem> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public List getGroupItems(){
        return this.items;
    }

    public void clearAll() {
//        items.clear();
//        if(items.size() < 2){
//            return;
//        }

//        for(int i=0; i<items.size(); i++){
//            items.get(i).children.clear();
//        }
        this.items.clear();
        notifyDataSetChanged();
    }

    public static final int VIEW_TYPE_SECTION_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    @Override
    public int getItemViewType(int position) {
        for (int i = 0; i < items.size(); i++) {
            GroupItem group = items.get(i);
            if (position < 1) return VIEW_TYPE_SECTION_HEADER;
            position--;
            int childCount = group.children.size();
            if (position < childCount) return VIEW_TYPE_ITEM;
            position-=childCount;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case VIEW_TYPE_SECTION_HEADER :
                view = inflater.inflate(R.layout.view_section_header, parent, false);
                return new SectionHeaderViewHolder(view);
            case VIEW_TYPE_ITEM :
                view = inflater.inflate(R.layout.view_section_child, parent, false);
                ItemViewHolder holder = new ItemViewHolder(view, parent.getContext());
                holder.setOnItemClickListener(this);
                holder.setOnItemLongClickListener(this);
                holder.setOnLikeClickListener(this);
                return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        for (int i = 0; i < items.size(); i++) {
            GroupItem g = items.get(i);
            if (position < 1) {
                ((SectionHeaderViewHolder)holder).setGroupItem(g);
                return;
            }
            position--;
            int childCount = g.children.size();
            if (position < childCount) {
                FriendsResult child = g.children.get(position);
                ((ItemViewHolder)holder).setChildItem(child);
                return;
            }
            position-=childCount;
        }
    }

    @Override
    public int getItemCount() {
        int totalCount = 0;
        for (GroupItem g : items) {
            totalCount += (1 + g.children.size());
        }
        return totalCount;
    }

    private int totalCount; //학교사람들 전체 토탈
    public int getTotalCount(){
//        return getItemCount() - this.items.size();  //total - 그룹 갯수
        return this.totalCount;
    }
    public void setTotalCount(int count){
        this.totalCount = count;
    }

    public final static int GROUP_PROFILE = 0;
    public final static int GROUP_FRIENDS = 1;
    public FriendsResult getItem(int position) {
        if(position < 0 || position > getItemCount()){
            return null;
        } else if(position < 2) {
            return items.get(GROUP_PROFILE).children.get(position-1);
        }
        return items.get(GROUP_FRIENDS).children.get(position-3); //postion 3 == 0헤더 1차일드뷰 2헤더
    }

    public List getFriendsList(){
        return this.items;
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MyFriendsAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MyFriendsAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}

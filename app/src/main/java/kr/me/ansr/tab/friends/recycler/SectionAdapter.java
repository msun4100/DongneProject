package kr.me.ansr.tab.friends.recycler;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.me.ansr.R;
import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-07-20.
 */
public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnItemClickListener, OnItemLongClickListener, ItemViewHolder.OnLikeClickListener{
    public List<GroupItem> items = new ArrayList<GroupItem>();
    public int blockCount = 0;

    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(SectionAdapter adapter, View view, FriendsResult item, int type);
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
                        Log.e("sectionAdapter:", "index:"+index +" "+fr.toString());
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

    public void addAllFriends(List<FriendsResult> items) {
        this.items.get(GROUP_FRIENDS).children.addAll(items);
        notifyDataSetChanged();
    }

    public void addAll(List<GroupItem> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public static final int VIEW_TYPE_SECTION_HEADER = 1000;
    public static final int VIEW_TYPE_ITEM = 1001;

    @Override
    public int getItemViewType(int position) {
        for (int i = 0; i < items.size(); i++) {
            GroupItem group = items.get(i);
            if (position < 1) {
//                Log.d("sectionAdpater", " getItemViewType: group"); //2번씩 호출되는게 맞구나..
                return VIEW_TYPE_SECTION_HEADER;
            }
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
//                if (!TextUtils.isEmpty(holder..image)) {
//                    Glide.with(getContext())
//                            .load(item.image)
//                            .into(iconView);
//                } else {
//                    iconView.setImageResource(R.mipmap.ic_launcher);
//                }
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
        //block된 유저는 아답터에 add하지 않았으므로 pass할때마다(for loop에서 continue 할때마다)
        //block 카운트를 증가시킴. 그리고 getItemCount()를 리턴할때 blockCount를 더해서 리턴
        //그래야 학교사람들 전체 토탈이랑 맞출 수 있음.
//        return totalCount + blockCount;
        //그냥 스크롤리스너에서 total + block 해서 검사
        return  totalCount;
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

    public List getGroupItems(){
        return this.items;
    }
}

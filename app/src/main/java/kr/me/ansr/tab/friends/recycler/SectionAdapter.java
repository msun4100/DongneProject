package kr.me.ansr.tab.friends.recycler;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-07-20.
 */
public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnItemClickListener, OnItemLongClickListener, ItemViewHolder.OnLikeClickListener{
    List<GroupItem> items = new ArrayList<GroupItem>();

    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(SectionAdapter adapter, View view, ChildItem item, int type);
    }
    OnAdapterItemClickListener mListener;
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onLikeClick(View v, ChildItem item, int type) {
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



    public void put(String groupName, String childName) {
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
        if (!TextUtils.isEmpty(childName)) {
            ChildItem child = new ChildItem();
            child.name = childName;
//            child.fontSize = 20 + r.nextInt(20);
            group.children.add(child);
        }

        notifyDataSetChanged();
    }
    public void put(String groupName, ChildItem child) {
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
                ChildItem child = g.children.get(position);
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

    public final static int GROUP_PROFILE = 0;
    public final static int GROUP_FRIENDS = 1;
    public ChildItem getItem(int position) {
        if(position < 0 || position > getItemCount()){
            return null;
        } else if(position < 2) {
            return items.get(0).children.get(position-1);
        }
        return items.get(1).children.get(position-3); //postion 3 == 0헤더 1차일드뷰 2헤더
    }

}

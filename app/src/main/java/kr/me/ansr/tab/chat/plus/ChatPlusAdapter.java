package kr.me.ansr.tab.chat.plus;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kr.me.ansr.R;
import kr.me.ansr.tab.friends.model.FriendsResult;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;

/**
 * Created by KMS on 2016-09-06.
 */
public class ChatPlusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnItemClickListener, OnItemLongClickListener, ItemViewHolder.OnLikeClickListener{
    //    public List<GroupItem> items = new ArrayList<GroupItem>();
    public List<FriendsResult> items = new ArrayList<FriendsResult>();

    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(ChatPlusAdapter adapter, View view, FriendsResult item, int type);
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

        //checkable
        if (checkMode == MODE_SINGLE) {
            if (mCheckedPosition != position) {
                mCheckedPosition = position;
                notifyDataSetChanged();
            }
        } else if (checkMode == MODE_MULTIPLE) {
            boolean oldChecked = checkedItems.get(position);
            checkedItems.put(position, !oldChecked);
            notifyDataSetChanged();
        }

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
    public void clear(){
        items.clear();
    }

    public void addAllFriends(List<FriendsResult> items) {
//        this.items.get(GROUP_FRIENDS).children.addAll(items);
        this.items.addAll(items);
        notifyDataSetChanged();
    }


    public static final int VIEW_TYPE_SECTION_HEADER = 1000;
    public static final int VIEW_TYPE_ITEM = 1001;


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_chat_plus_child, parent, false);
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
        //위에 2라인은 기존 코드 아래는 checked구현위해 추가한 코드
        if (checkMode == MODE_SINGLE) {
            if (position == mCheckedPosition) {
                ((ItemViewHolder)holder).setChecked(true);
            } else {
                ((ItemViewHolder)holder).setChecked(false);
            }
        } else if (checkMode == MODE_MULTIPLE) {
            ((ItemViewHolder)holder).setChecked(checkedItems.get(position));
        }
        return;
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

    //for checkable items
    SparseBooleanArray checkedItems = new SparseBooleanArray();
    int mCheckedPosition = INVALID_POSITION;
    int checkMode;

    public static final int INVALID_POSITION = -1;
    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTIPLE = 1;

    public void setItemCheck(int position, boolean checked) {
        if (checkMode == MODE_SINGLE) {
            if (mCheckedPosition != position && checked) {
                mCheckedPosition = position;
                notifyDataSetChanged();
            } else if (mCheckedPosition == position && !checked) {
                mCheckedPosition = INVALID_POSITION;
                notifyDataSetChanged();
            }
        } else if (checkMode == MODE_MULTIPLE) {
            checkedItems.put(position, checked);
            notifyDataSetChanged();
        }
    }

    public int getCheckItemPosition() {
        if (checkMode == MODE_SINGLE) {
            return mCheckedPosition;
        } else {
            return INVALID_POSITION;
        }
    }

    public SparseBooleanArray getCheckedItemPositions() {
        return checkedItems;
    }
    public void setMode(int mode) {
        if (mode == MODE_SINGLE || mode == MODE_MULTIPLE) {
            checkMode = mode;
        } else {
            throw new IllegalArgumentException("invalid check mode");
        }
    }

    public int getMode() {
        return checkMode;
    }
}

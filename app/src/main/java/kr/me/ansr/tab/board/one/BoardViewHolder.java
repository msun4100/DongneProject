package kr.me.ansr.tab.board.one;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import kr.me.ansr.R;
import kr.me.ansr.image.upload.Config;

/**
 * Created by KMS on 2016-07-27.
 */
public class BoardViewHolder extends RecyclerView.ViewHolder{

    Context mContext;
    ImageView iconThumbView;
    TextView nameView;
    ListView listView;
    ArrayAdapter<String> mAdapter;
    LinearLayout listViewLayout;

    public OnItemClickListener itemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }
    public OnItemLongClickListener itemLongClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        itemLongClickListener = listener;
    }

    public BoardViewHolder(View itemView, Context context) {
        super(itemView);
        this.mContext = context;
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                if(itemLongClickListener != null){
                    itemLongClickListener.onItemLongClick(v, getAdapterPosition());
                }
                return true;
            }
        });
        iconThumbView = (ImageView)itemView.findViewById(R.id.image_board_icon);
        nameView = (TextView)itemView.findViewById(R.id.text_board_name);
        listView = (ListView)itemView.findViewById(R.id.listView_board);
		mAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onLikeClick(view, mItem, 300);
            }
        });

//        listViewLayout = (LinearLayout)itemView.findViewById(R.id.linear_board_reply_layout);
//        listViewLayout.setOnClickListener(viewListener);
        iconThumbView.setOnClickListener(viewListener);
        nameView.setOnClickListener(viewListener);

    }

    public void setBoardItem(BoardResult item) {
//        titleView.setTextSize(item.fontSize);
        listView.setVisibility(View.GONE);
        this.mItem = item;
        nameView.setText(item.username);
        if (!TextUtils.isEmpty(item.pic)) {
            String url = Config.FILE_GET_URL.replace(":userId", ""+item.userId);
            Glide.with(mContext).load(url).into(iconThumbView);
        } else {
            iconThumbView.setImageResource(R.mipmap.ic_launcher);
        }

        if(item.repArr != null){
            listView.setVisibility(View.VISIBLE);
            mAdapter.clear();
            for(String str : item.repArr){
//              termText.setText(Html.fromHtml("<u>" + str + "</u>"));
                mAdapter.add(str);
            }
            mAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnItems(listView);    //listAdapter가 null이 아니면 true 리턴
        }

    }

    public View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.text_board_name:
                    if (mListener != null) {
                        mListener.onLikeClick(v, mItem, 100);
                    }
                    break;
                case R.id.image_board_icon:
                    if (mListener != null) {
                        mListener.onLikeClick(v, mItem, 200);
                    }
                    break;
//                case R.id.linear_board_reply_layout:
//                    if (mListener != null) {
//                        mListener.onLikeClick(v, mItem, 300);
//                    }
//                    break;
                default:
                    break;
            }
        }
    };

    //for individual row item button click
    BoardResult mItem;
    public interface OnLikeClickListener {
        public void onLikeClick(View v, BoardResult item, int type);
    }
    OnLikeClickListener mListener;
    public void setOnLikeClickListener(OnLikeClickListener listener) {
        mListener = listener;
    }


    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();
            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }
            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() * (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;
        } else {
            return false;
        }
    }
}


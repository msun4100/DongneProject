package kr.me.ansr.tab.board.one;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.me.ansr.MyApplication;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.board.reply.PreReplyAdapter;
import kr.me.ansr.tab.board.reply.ReplyResult;

/**
 * Created by KMS on 2016-07-27.
 */
public class BoardViewHolder extends RecyclerView.ViewHolder{

    Context mContext;
    ImageView iconThumb;
    TextView nameView;
    TextView stuIdView;
    TextView deptView;
    TextView timeStampView;
    TextView bodyView;
    TextView bodyAddView;
    ImageView iconReply;
    TextView replyCountView;
    ImageView iconLike;
    TextView likeCountView;

    ListView listView;
    PreReplyAdapter mAdapter;
    LinearLayout listViewLayout;
    LinearLayout likeLayout;

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
        iconThumb = (ImageView)itemView.findViewById(R.id.image_board_thumb);
        nameView = (TextView)itemView.findViewById(R.id.text_board_name);
        stuIdView = (TextView)itemView.findViewById(R.id.text_board_stuid);
        deptView = (TextView)itemView.findViewById(R.id.text_board_dept);
        timeStampView = (TextView)itemView.findViewById(R.id.text_board_timestamp);
        bodyView = (TextView)itemView.findViewById(R.id.text_board_body);
        bodyAddView = (TextView)itemView.findViewById(R.id.text_board_body_add);
        iconReply = (ImageView)itemView.findViewById(R.id.image_board_reply);
        replyCountView = (TextView)itemView.findViewById(R.id.text_board_reply_count);
        iconLike = (ImageView)itemView.findViewById(R.id.image_board_like);
        likeCountView = (TextView)itemView.findViewById(R.id.text_board_like_count);

        //for adapter item click
        likeLayout = (LinearLayout)itemView.findViewById(R.id.linear_like_layout);

        listView = (ListView)itemView.findViewById(R.id.listView_board);
		mAdapter = new PreReplyAdapter(context);
        mAdapter.setOnAdapterItemClickListener(new PreReplyAdapter.OnAdapterItemClickListener() {
            @Override
            public void onAdapterItemClick(PreReplyAdapter adapter, View view, ReplyResult item, int type) {
                mListener.onLikeClick(view, getAdapterPosition(), mItem, 300);
            }
        });
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onLikeClick(view, getAdapterPosition(), mItem, 300);
            }
        });

//        listViewLayout = (LinearLayout)itemView.findViewById(R.id.linear_board_reply_layout);
//        listViewLayout.setOnClickListener(viewListener);
        iconThumb.setOnClickListener(viewListener);
        nameView.setOnClickListener(viewListener);
        likeLayout.setOnClickListener(viewListener);

    }

    public void setBoardItem(BoardResult item) {
//        titleView.setTextSize(item.fontSize);
        listView.setVisibility(View.GONE);
        bodyAddView.setVisibility(View.GONE);
        this.mItem = item;
        nameView.setText(item.user.username);
        bodyView.setText(item.body);
        if(bodyView.getText().toString().length() > 80){
            bodyAddView.setVisibility(View.VISIBLE);
        }
        String url = Config.FILE_GET_URL.replace(":userId", ""+item.writer).replace(":size", "small");
        //isEmpty() 체크를 안하면 pic="" 인 유저들은 placeholder이미지가 뿌려짐
        Glide.with(mContext).load(url).placeholder(R.drawable.b_main_view_contents_icon_05_on).into(iconThumb);
//        if (!TextUtils.isEmpty(item.user.pic)) {
//            String url = Config.FILE_GET_URL.replace(":userId", ""+item.writer);
//            Glide.with(mContext).load(url).placeholder(R.drawable.b_main_view_contents_icon_05_on).into(iconThumb);
//
//        } else {
//            iconThumb.setImageResource(R.mipmap.ic_launcher);
//        }
        nameView.setText(item.user.username);
        String stuId = String.valueOf(item.user.enterYear);
        if(stuId.length()==4){
            stuIdView.setText(stuId.substring(2,4));    //2016 --> 16
        } else { stuIdView.setText("17"); }
        deptView.setText(item.user.deptname);
        timeStampView.setText(MyApplication.getTimeStamp(item.createdAt));
        if(item.likes.contains(Integer.valueOf(PropertyManager.getInstance().getUserId()))){
            iconLike.setImageResource(R.drawable.b_main_view_contents_icon_05_on);
        } else {iconLike.setImageResource(R.drawable.b_main_view_contents_icon_05_off);}
        likeCountView.setText(""+item.likeCount);
        replyCountView.setText("0");
//        iconReply...
//        iconLike...
        //preReplies 처리
        if(item.preReplies != null){
            replyCountView.setText(""+item.repCount);
            listView.setVisibility(View.VISIBLE);
            mAdapter.clear();
            for(ReplyResult reply : item.preReplies){
                mAdapter.add(reply);
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
                        mListener.onLikeClick(v, getAdapterPosition(), mItem, 100);
                    }
                    break;
                case R.id.image_board_thumb:
                    if (mListener != null) {
                        mListener.onLikeClick(v, getAdapterPosition(), mItem, 200);
                    }
                    break;
                case R.id.linear_like_layout:
                    if (mListener != null) {
                        mListener.onLikeClick(v, getAdapterPosition(), mItem, 400);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //for individual row item button click
    BoardResult mItem;
    public interface OnLikeClickListener {
        public void onLikeClick(View v, int position, BoardResult item, int type);
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


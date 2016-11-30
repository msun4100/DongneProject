package kr.me.ansr.gcmchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.me.ansr.MyApplication;
import kr.me.ansr.R;
import kr.me.ansr.gcmchat.model.Message;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;


public class ChatRoomThreadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
implements OnItemClickListener, OnItemLongClickListener, OtherViewHolder.OnLikeClickListener{

    public interface OnAdapterItemClickListener {
        public void onAdapterItemClick(ChatRoomThreadAdapter adapter, View view, Message item, int type);
    }
    OnAdapterItemClickListener mListener;
    public void setOnAdapterItemClickListener(OnAdapterItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onLikeClick(View v, Message item, int type) {
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
    }

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

    private static String TAG = ChatRoomThreadAdapter.class.getSimpleName();

    private String userId;
    private final static int SELF = 100;
    private final static int OTHER = 200;
    private final static int LOG = 300;
    private static String today;

    private Context mContext;
    private ArrayList<Message> messageArrayList;


    public int logCount = 0; //on 1130
    int total;
    public int getTotalCount() {
        return total;
    }
    public void setTotalCount(int total) {
        this.total = total;
    }




//    public class ViewHolder extends RecyclerView.ViewHolder {
//        TextView message, timestamp;
//        ImageView thumb;
//        public ViewHolder(View view) {
//            super(view);
//            message = (TextView) itemView.findViewById(R.id.message);
//            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
//            thumb = (ImageView) itemView.findViewById(R.id.thumb);
//        }
//    }


    public ChatRoomThreadAdapter(Context mContext, ArrayList<Message> messageArrayList, String userId) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView;
//        // view type is to identify where to render the chat message
//        // left or right
//        if (viewType == SELF) {
//            // self message
//            itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.a_chat_item_self, parent, false);
//        } else {
//            // others message
//            itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.a_chat_item_other, parent, false);
//        }
//        return new ViewHolder(itemView);
//    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case SELF :
                view = inflater.inflate(R.layout.a_chat_item_self, parent, false);
                ItemViewHolder selfHolder = new ItemViewHolder(view, parent.getContext());
//                selfHolder.setOnItemClickListener(this);
                selfHolder.setOnItemLongClickListener(this);
//                selfHolder.setOnLikeClickListener(this);
                return selfHolder;
            case OTHER:
                view = inflater.inflate(R.layout.a_chat_item_other, parent, false);
                OtherViewHolder holder = new OtherViewHolder(view, parent.getContext());
                holder.setOnItemClickListener(this);
                holder.setOnItemLongClickListener(this);
                holder.setOnLikeClickListener(this);
                return holder;
            case LOG:
                view = inflater.inflate(R.layout.a_chat_item_log, parent, false);
                LogViewHolder logHolder = new LogViewHolder(view, parent.getContext());
                return logHolder;
            default:
                break;

        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        String id = ""+message.getUser().getId();
        int isLog = message.bgColor;
        if(isLog == 1){
//            Log.d(TAG, "getItemViewType: "+"isLog "+message.toString());
            return LOG;
        } else {
            if(id.equals(userId)){
                return SELF;
            } else {
                return OTHER;
            }
        }
//        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Message message = messageArrayList.get(position);
        int type = getItemViewType(position);
        switch (type){
            case SELF:
                ((ItemViewHolder)holder).setChildItem(message);
                break;
            case OTHER:
                ((OtherViewHolder)holder).setChildItem(message);
                break;
            case LOG:
                ((LogViewHolder)holder).setChildItem(message);
                break;
            default:
                break;
        }
        return;
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
//        2016-07-06T05:47:19.000Z
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

}


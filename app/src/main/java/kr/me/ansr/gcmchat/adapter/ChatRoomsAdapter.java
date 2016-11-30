package kr.me.ansr.gcmchat.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import kr.me.ansr.MyApplication;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.gcmchat.model.ChatRoom;
import kr.me.ansr.image.CustomBitmapPool;
import kr.me.ansr.image.upload.Config;

public class ChatRoomsAdapter extends RecyclerView.Adapter<ChatRoomsAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ChatRoom> chatRoomArrayList;
    private static String today;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, message, timestamp, count;
        public ImageView thumbIcon;
        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            message = (TextView) view.findViewById(R.id.message);
            timestamp = (TextView) view.findViewById(R.id.timestamp);
            count = (TextView) view.findViewById(R.id.count);
            thumbIcon = (ImageView) view.findViewById(R.id.thumb);
        }
    }


    public ChatRoomsAdapter(Context mContext, ArrayList<ChatRoom> chatRoomArrayList) {
        this.mContext = mContext;
        this.chatRoomArrayList = chatRoomArrayList;
//        this.originItems = chatRoomArrayList;

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.a_chat_rooms_list_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatRoom chatRoom = chatRoomArrayList.get(position);
        holder.name.setText(chatRoom.getName());
        holder.message.setText(chatRoom.getLastMessage());
        if (chatRoom.getUnreadCount() > 0) {
//            holder.count.setText(String.valueOf(chatRoom.getUnreadCount()));
            holder.count.setText("N");
            holder.count.setVisibility(View.VISIBLE);
        } else {
            holder.count.setVisibility(View.INVISIBLE);
        }

//        holder.timestamp.setText(getTimeStamp(chatRoom.getTimestamp()));
        holder.timestamp.setText(MyApplication.getTimeStamp(chatRoom.getTimestamp()));
        if(chatRoom.users.size() <= 2){
            String selfUserId = PropertyManager.getInstance().getUserId();
            String targetId = "";
            for(int userId : chatRoom.users){
                if(!selfUserId.equals(""+userId)){
                    targetId = ""+userId;
                    String url = Config.FILE_GET_URL.replace(":userId", ""+targetId).replace(":size", "small");
                    Glide.with(mContext).load(url).placeholder(R.drawable.e__who_icon).centerCrop()
                            .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool()))
                            .into(holder.thumbIcon);
                    break;
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return chatRoomArrayList.size();
    }

    public static String getTimeStamp(String dateStr) {
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

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ChatRoomsAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ChatRoomsAdapter.ClickListener clickListener) {
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

    public boolean isSearching = false;
    private ArrayList<ChatRoom> filterList = new ArrayList<ChatRoom>();
    // Do Search...
    public boolean filter(final String text, final ArrayList<ChatRoom> originList) {
        // Searching could be complex.. so this job will dispatch it to a runnable thread...

        new Thread(new Runnable() {
            boolean bool;

            @Override
            public void run() {
                // Clear the filter list
                bool = false;
                filterList.clear();
                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {
                    filterList.addAll(originList);
                    bool = false;
                } else {
                    // Iterate in the original List and add it to filter list...
                    for (ChatRoom cr : originList) {
                        if (cr.name.toLowerCase().contains(text.toLowerCase()) ) {
//                        if (cr.name.contains(text) ) {
                            // Adding Matched items
                            filterList.add(cr);
                            bool = true;
                        }
                    }
                }
                chatRoomArrayList.clear();  //adapter에 쓰이는 리스트
                chatRoomArrayList.addAll(filterList);
//                Log.e("mAdpater", "run0: "+ originList.toString() );
//                Log.e("mAdpater", "run1: "+ chatRoomArrayList.toString() );
//                Log.e("mAdpater", "run2: "+ filterList.toString() );
                // Set on UI Thread
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                        isSearching = bool;
                    }
                });
//                isSearching = bool;
            }
        }).start();
        return isSearching;
    }

}

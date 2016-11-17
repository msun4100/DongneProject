package kr.me.ansr.gcmchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import kr.me.ansr.MyApplication;
import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.gcmchat.model.Message;
import kr.me.ansr.image.CustomBitmapPool;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;

/**
 * Created by KMS on 2016-11-14.
 */
public class LogViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    TextView messageView;

    public OnItemClickListener itemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public OnItemLongClickListener itemLongClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        itemLongClickListener = listener;
    }

    public LogViewHolder(View itemView, Context context) {
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

        messageView = (TextView)itemView.findViewById(R.id.text_log);

//        iconThumbView.setOnClickListener(viewListener);
//        nameView.setOnClickListener(viewListener);

    }

    public void setChildItem(Message message) {
        mItem = message;    //1114
        String timestamp;
        if(message.getCreatedAt() != null){
            timestamp = getTimeStamp(message.getCreatedAt());
        } else {
            timestamp = "timeStamp ";
        }
        messageView.setText(timestamp);

    }

    public View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.thumb:
                    if (mListener != null) {
                        mListener.onLikeClick(v, mItem, 100);
                    }
                    break;
                case R.id.username:
                    if (mListener != null) {
                        mListener.onLikeClick(v, mItem, 200);
                    }
                    break;
                default:
                    break;
            }

        }
    };

    //for individual row item button click
    Message mItem;
    public interface OnLikeClickListener {
        public void onLikeClick(View v, Message item, int type);
    }
    OnLikeClickListener mListener;
    public void setOnLikeClickListener(OnLikeClickListener listener) {
        mListener = listener;
    }

    public String getTimeStamp(String dateStr) {	//dateStr == Server's timeStamp.(UTC)
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getDefault());	// == TimeZone.getTimeZone("Asia/Seoul")
        String today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");	//UTC 포맷
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = "";
        today = today.length() < 2 ? "0" + today : today;
        try {
            Date date = format.parse(dateStr);	//UTC기준 dateStr을 parsing.
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            yearFormat.setTimeZone(TimeZone.getDefault());
            String dateYear = yearFormat.format(date);
            if(year.equals(dateYear)){
                SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
                todayFormat.setTimeZone(TimeZone.getDefault());
                String dateToday = todayFormat.format(date);
                format = dateToday.equals(today) ? new SimpleDateFormat("yyyy.MM.dd E") : new SimpleDateFormat("yyyy.MM.dd E");
                String date1 = format.format(date);
                timestamp = date1.toString()  + "요일";
            } else {
//				Log.e("year check", "false");
                SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
                todayFormat.setTimeZone(TimeZone.getDefault());
                format = new SimpleDateFormat("yyyy.MM.dd E");
                String date1 = format.format(date);
                timestamp = date1.toString() + "요일";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

}

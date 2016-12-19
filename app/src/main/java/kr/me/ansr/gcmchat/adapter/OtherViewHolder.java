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
public class OtherViewHolder extends RecyclerView.ViewHolder{
    //    glide호출하기 위해서 ItemViewHolder 생성자에 parent.getContext()를 넣어서 콘텍스트를 같이보냄
// 생성자에서 mContext = context(parent.getContext()로 보낸 컨텍스트)를 하고 setChildItem에서 mContext를 사용해서 글라이드 호출
    Context mContext;
    ImageView iconThumbView;
    TextView messageView;
    TextView timeStampView;
    TextView nameView;

    public OnItemClickListener itemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public OnItemLongClickListener itemLongClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        itemLongClickListener = listener;
    }

    public OtherViewHolder(View itemView, Context context) {
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
        iconThumbView = (ImageView)itemView.findViewById(R.id.thumb);
        messageView = (TextView)itemView.findViewById(R.id.message);
        timeStampView = (TextView) itemView.findViewById(R.id.timestamp);
        nameView = (TextView) itemView.findViewById(R.id.username);


        iconThumbView.setOnClickListener(viewListener);
        nameView.setOnClickListener(viewListener);

    }

    public void setChildItem(Message message) {
        mItem = message;    //1114
        messageView.setText(message.getMessage());
        nameView.setText(message.getUser().getName());
        String timestamp;
        if(message.getCreatedAt() != null){
//            timestamp = MyApplication.getTimeStamp(message.getCreatedAt());
            timestamp = getTimeStamp(message.getCreatedAt());
        } else {
            timestamp = "timeStamp";
        }
        timeStampView.setText(timestamp);
        int userId = Integer.parseInt(PropertyManager.getInstance().getUserId());
        if(message.getUser().getId() != userId){
            int id = message.getUser().getId();
            String url = Config.FILE_GET_URL.replace(":userId", ""+id).replace(":size", "small");
            Glide.with(mContext).load(url).placeholder(R.drawable.e__who_icon).centerCrop()
//                    .bitmapTransform(new CropCircleTransformation(new CustomBitmapPool()))
                    .into(iconThumbView);
        }
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

    //무조건 오후 xx:xx 리턴. 날짜는 로그에 기록
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
                format = dateToday.equals(today) ? new SimpleDateFormat("a hh:mm") : new SimpleDateFormat("a hh:mm");
                String date1 = format.format(date);
                timestamp = date1.toString();
            } else {
//				Log.e("year check", "false");
                SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
                todayFormat.setTimeZone(TimeZone.getDefault());
                format = new SimpleDateFormat("yyyy.MM.dd a hh:mm");
                String date1 = format.format(date);
                timestamp = date1.toString();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

}

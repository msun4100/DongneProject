package kr.me.ansr.tab.meet;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;


import kr.me.ansr.MyApplication;
import kr.me.ansr.R;
import kr.me.ansr.database.Push;
import kr.me.ansr.gcmchat.gcm.NotificationUtils;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.recycler.OnItemClickListener;
import kr.me.ansr.tab.friends.recycler.OnItemLongClickListener;

/**
 * Created by KMS on 2016-09-22.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder{
    //    glide호출하기 위해서 ItemViewHolder 생성자에 parent.getContext()를 넣어서 콘텍스트를 같이보냄
// 생성자에서 mContext = context(parent.getContext()로 보낸 컨텍스트)를 하고 setChildItem에서 mContext를 사용해서 글라이드 호출
    Context mContext;

    LinearLayout bgView;

    ImageView iconThumbView;
    TextView nameView;
    TextView msgView;
    TextView timeStampView;


    public OnItemClickListener itemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
    }

    public OnItemLongClickListener itemLongClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        itemLongClickListener = listener;
    }

    public ItemViewHolder(View itemView, Context context) {
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
        iconThumbView = (ImageView)itemView.findViewById(R.id.image_feed_icon);
        nameView = (TextView)itemView.findViewById(R.id.text_feed_name);
        msgView = (TextView)itemView.findViewById(R.id.text_feed_msg);
        timeStampView = (TextView)itemView.findViewById(R.id.text_feed_timestamp);
        bgView = (LinearLayout) itemView.findViewById(R.id.linear_feed_layout);
        iconThumbView.setOnClickListener(viewListener);
    }

    public void setChildItem(Push item) {

        mItem = item;
        nameView.setText(item.name);
        msgView.setText(item.message);
        timeStampView.setText(MyApplication.getTimeStamp(item.created_at));
        switch (item.bgColor){
            case 0:
                bgView.setBackgroundResource(R.drawable.z_feed_none_selected_rec);
                break;
            case 1:
                bgView.setBackgroundResource(R.drawable.z_feed_selected_rec);
                default:
                break;
        }

        String url = Config.FILE_GET_URL.replace(":userId", ""+item.user_id).replace(":size", "small");
        Glide.with(mContext).load(url)
                .placeholder(R.drawable.e__who_icon)
                .centerCrop()
                .signature(new StringSignature(item.created_at))
                .into(iconThumbView);
    }

    public View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.image_feed_icon:
                    if (mListener != null) {
                        mListener.onLikeClick(v, mItem, 100);
                    }
                    break;
                default:
                    break;
            }

        }
    };

    //for individual row item button click
    Push mItem;
    public interface OnLikeClickListener {
        public void onLikeClick(View v, Push item, int type);
    }
    OnLikeClickListener mListener;
    public void setOnLikeClickListener(OnLikeClickListener listener) {
        mListener = listener;
    }




}

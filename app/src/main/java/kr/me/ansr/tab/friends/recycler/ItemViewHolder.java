package kr.me.ansr.tab.friends.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import kr.me.ansr.R;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.friends.model.FriendsResult;

/**
 * Created by KMS on 2016-07-20.
 */
public class ItemViewHolder extends RecyclerView.ViewHolder{
//    glide호출하기 위해서 ItemViewHolder 생성자에 parent.getContext()를 넣어서 콘텍스트를 같이보냄
// 생성자에서 mContext = context(parent.getContext()로 보낸 컨텍스트)를 하고 setChildItem에서 mContext를 사용해서 글라이드 호출
    Context mContext;

    ImageView iconThumbView;
    ImageView iconAddView;
    TextView nameView;
    TextView deptView;
    TextView stuIdView;
    TextView jobView;
    TextView distanceView;

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
        iconThumbView = (ImageView)itemView.findViewById(R.id.image_friends_icon);
        iconAddView = (ImageView)itemView.findViewById(R.id.image_friends_add);
        nameView = (TextView)itemView.findViewById(R.id.text_friends_name);
        deptView = (TextView)itemView.findViewById(R.id.text_friends_dept);
        stuIdView = (TextView)itemView.findViewById(R.id.text_friedns_stuid);
        jobView = (TextView)itemView.findViewById(R.id.text_friends_job);
        distanceView = (TextView)itemView.findViewById(R.id.text_friends_distance);

        iconThumbView.setOnClickListener(viewListener);
        nameView.setOnClickListener(viewListener);

    }

    public void setChildItem(FriendsResult item) {
//        titleView.setTextSize(item.fontSize);
//        iconView.setImageResource(item.iconId);
        mItem = item;

        if(item.status == 1){
            iconAddView.setVisibility(View.GONE);
            nameView.setVisibility(View.VISIBLE);
        } else {
            iconAddView.setVisibility(View.VISIBLE);
            nameView.setVisibility(View.GONE);
        }
        nameView.setText(item.username);
        String stuId = String.valueOf(item.univ.get(0).getEnterYear());
        stuIdView.setText(stuId.substring(2,4));    //2016 --> 16
        deptView.setText(item.univ.get(0).getDeptname());
        jobView.setText(""+item.job.getName() + " " + item.job.getTeam());
        distanceView.setText(item.temp);
//        String url = Config.FILE_GET_URL.replace(":userId", ""+item.userId).replace(":size", "small");
//        Glide.with(mContext).load(url).into(iconThumbView);
        if (!TextUtils.isEmpty(item.pic.small)) {
            String url = Config.FILE_GET_URL.replace(":userId", ""+item.userId).replace(":size", "small");
            Glide.with(mContext).load(url)
                    .placeholder(R.drawable.e__who_icon)
                    .centerCrop()
                    .signature(new StringSignature(item.getUpdatedAt()))
                    .into(iconThumbView);
        } else {
            iconThumbView.setImageResource(R.drawable.e__who_icon);
        }

    }

    public View.OnClickListener viewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.text_friends_name:
                    if (mListener != null) {
                        mListener.onLikeClick(v, mItem, 100);
                    }
                    break;
                case R.id.image_friends_icon:
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
    FriendsResult mItem;
    public interface OnLikeClickListener {
        public void onLikeClick(View v, FriendsResult item, int type);
    }
    OnLikeClickListener mListener;
    public void setOnLikeClickListener(OnLikeClickListener listener) {
        mListener = listener;
    }


}

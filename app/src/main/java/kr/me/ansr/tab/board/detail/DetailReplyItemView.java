package kr.me.ansr.tab.board.detail;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import kr.me.ansr.PropertyManager;
import kr.me.ansr.R;
import kr.me.ansr.image.upload.Config;
import kr.me.ansr.tab.board.reply.ReplyResult;

/**
 * Created by KMS on 2016-07-27.
 */
public class DetailReplyItemView extends FrameLayout{
    public DetailReplyItemView(Context context) {
        super(context);
        init();
    }

    public DetailReplyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    ReplyResult mItem;
    TextView usernameView;
    TextView bodyView;

    ImageView iconReply, iconThumb;
    TextView replyCountView;
    ImageView iconLike;
    TextView likeCountView;

    LinearLayout likeLayout;


    FrameLayout reportLayout;

    public interface OnLikeClickListener {
        public void onLikeClick(View v, ReplyResult item, int type);
    }
    OnLikeClickListener mListener;
    public void setOnLikeClickListener(OnLikeClickListener listener) {
        mListener = listener;
    }
    // ==================================================
    public OnClickListener viewListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.text_board_reply_reply_name:
                    if (mListener != null) {
                        mListener.onLikeClick(DetailReplyItemView.this, mItem, 100);
                    }
                    break;
                case R.id.text_board_reply_reply_content:
                    if (mListener != null) {
                        mListener.onLikeClick(DetailReplyItemView.this, mItem, 200);
                    }
                    break;
                case R.id.linear_board_reply_like_layout:
                    if (mListener != null) {
                        mListener.onLikeClick(DetailReplyItemView.this, mItem, 300);
                    }
                    break;
                case R.id.frame_board_reply_reply_report:
                    if (mListener != null) {
                        mListener.onLikeClick(DetailReplyItemView.this, mItem, 400);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void init() {
        LayoutInflater.from(getContext()).inflate( R.layout.view_board_detail_reply_layout, this);
        iconThumb = (ImageView)findViewById(R.id.image_board_reply_thumb) ;

        usernameView = (TextView)findViewById(R.id.text_board_reply_reply_name);
        bodyView = (TextView)findViewById(R.id.text_board_reply_reply_content);
        usernameView.setOnClickListener(viewListener);
        bodyView.setOnClickListener(viewListener);

        iconReply = (ImageView)findViewById(R.id.image_board_reply_reply);
        replyCountView = (TextView)findViewById(R.id.text_board_reply_reply_count);
        iconLike = (ImageView) findViewById(R.id.image_board_reply_like);
        likeCountView = (TextView)findViewById(R.id.text_board_reply_like_count);

        likeLayout = (LinearLayout)findViewById(R.id.linear_board_reply_like_layout);
        likeLayout.setOnClickListener(viewListener);

        reportLayout = (FrameLayout)findViewById(R.id.frame_board_reply_reply_report);
        reportLayout.setOnClickListener(viewListener);

    }

    public void setItemData(ReplyResult item) {
        mItem = item;
//        termText.setText(Html.fromHtml("<u>" + str + "</u>"));
        switch (item.type){
            case "10":
            case "00":
//                usernameView.setText(Html.fromHtml("<B>"+getResources().getString(R.string.board_anonymous_name)+"</B>"));
                usernameView.setText("익 명");
                iconThumb.setImageResource(R.drawable.e__who_icon);
                break;
            default:
//                usernameView.setText(Html.fromHtml("<B>"+item.username+"</B>"));
                usernameView.setText(item.username);
                String url = Config.FILE_GET_URL.replace(":userId", ""+item.userId).replace(":size", "small");
                Glide.with(getContext()).load(url).placeholder(R.drawable.e__who_icon).centerCrop().into(iconThumb);
                break;
        }
        bodyView.setText(item.body);

        if(item.likes.contains(Integer.valueOf(PropertyManager.getInstance().getUserId()))){
            iconLike.setImageResource(R.drawable.e__like_2);
        } else {iconLike.setImageResource(R.drawable.e__like);}
        likeCountView.setText(""+item.likes.size());
        replyCountView.setText("0");
        if(item.replies != null) {
            replyCountView.setText("" + item.replies.size());
        }
    }

}

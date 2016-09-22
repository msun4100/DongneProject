package kr.me.ansr.tab.board.reply;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import kr.me.ansr.R;

/**
 * Created by KMS on 2016-07-27.
 */
public class PreReplyItemView extends FrameLayout{
    public PreReplyItemView(Context context) {
        super(context);
        init();
    }

    public PreReplyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // =======================================
    // ImageView backgroundView;
    ReplyResult mItem;
    TextView usernameView;
    TextView bodyView;


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
                case R.id.text_board_preview_reply_name:
                    if (mListener != null) {
                        mListener.onLikeClick(PreReplyItemView.this, mItem, 100);
                    }
                    break;
                case R.id.text_board_preview_reply_content:
                    if (mListener != null) {
                        mListener.onLikeClick(PreReplyItemView.this, mItem, 200);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void init() {
        LayoutInflater.from(getContext()).inflate( R.layout.view_board_reply_layout, this);
        usernameView = (TextView)findViewById(R.id.text_board_preview_reply_name);
        bodyView = (TextView)findViewById(R.id.text_board_preview_reply_content);
        usernameView.setOnClickListener(viewListener);
        bodyView.setOnClickListener(viewListener);
    }

    public void setItemData(ReplyResult item) {
        mItem = item;
//        termText.setText(Html.fromHtml("<u>" + str + "</u>"));
        switch (item.type){
            case "10":
            case "00":
                usernameView.setText(Html.fromHtml("<B>"+getResources().getString(R.string.board_anonymous_name)+"</B>"));
                break;
            default:
                usernameView.setText(Html.fromHtml("<B>"+item.username+"</B>"));
                break;
        }
        bodyView.setText(item.body);
    }

}

package kr.me.ansr.tab.friends.model;
import kr.me.ansr.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * Created by KMS on 2016-08-05.
 */
public class SnsView extends FrameLayout {
    public SnsView(Context context) {
        super(context);
        init();
    }

    TextView urlView;
    ImageView iconView;
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_profile_sns_layout, this);
//        inflate(getContext(), R.layout.view_profile_sns_layout, this);
        iconView = (ImageView)findViewById(R.id.image_profile_sns_icon);
        urlView = (TextView)findViewById(R.id.text_profile_sns);
    }

    public void setData(Sns data) {
        if(data.url != null){
            urlView.setText(data.url);
        } else {
            urlView.setText("default text");
        }

        if(data.sns!= null && data.sns.equals("fb")){
            iconView.setImageResource(R.mipmap.ic_album);
        } else if(data.sns!= null && data.sns.equals("kakao")){
//            iconView.setImageDrawable(data.icon);
        } else {
            iconView.setImageResource(R.mipmap.ic_launcher);
        }

    }
}
